package com.app.requirements.visualization.config;

import com.app.requirements.visualization.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder managerBuilder) {
        managerBuilder.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) {
        // Ignore URLs with such patterns
        web.ignoring().antMatchers( "/css/**", "/webjars/**", "/html/**", "/js/**", "/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Set permissions on endpoints
        http.authorizeRequests().antMatchers(
                "/registration**").permitAll()
                // Our private endpoints
                .anyRequest().authenticated()
                // Disable CSRF and enable CORS
                .and().csrf().disable().cors()
                .and()
                // Handle login and log-out
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
        .permitAll();
    }

}
