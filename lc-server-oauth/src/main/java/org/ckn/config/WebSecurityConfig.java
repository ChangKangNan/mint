package org.ckn.config;

import org.ckn.filter.JWTAuthenticationFilter;
import org.ckn.jwt.endpoint.JWTAuthenticationEntryPoint;
import org.ckn.jwt.endpoint.JWTAuthorizationFilter;
import org.ckn.jwt.mobile.MobileAuthenticationProvider;
import org.ckn.jwt.password.UsernameAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new MobileAuthenticationProvider());
        UsernameAuthenticationProvider usernameAuthenticationProvider = new UsernameAuthenticationProvider();
        //????????????????????????
        usernameAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        //?????????????????????
        usernameAuthenticationProvider.setUserDetailsService(userDetailsService);
        auth.authenticationProvider(usernameAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()//csrf??????????????????POST??????
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()//login????????????token??????
                .anyRequest().authenticated()//??????????????????????????????
                .and()
                //???????????????
                .addFilterBefore(new JWTAuthenticationFilter(authenticationManager(), redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthorizationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//??????session
                .and()
                .exceptionHandling().authenticationEntryPoint(new JWTAuthenticationEntryPoint());//???????????????????????????????????????????????????
    }
}
