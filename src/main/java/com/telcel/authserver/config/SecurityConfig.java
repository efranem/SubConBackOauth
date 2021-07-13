package com.telcel.authserver.config;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.telcel.authserver.model.UserDetailsCustom;
import com.telcel.authserver.utils.Propiedades;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		//auth.inMemoryAuthentication()
    //    .withUser("EX407665").password(encoder().encode("Telcel123")).roles("ADMIN","USER").and()
		//		.withUser("EX4076651").password(encoder().encode("Telcel123")).roles("USER");

		auth.authenticationProvider(new AuthenticationProvider() {
			@Override
			public boolean supports(Class<?> authentication) {
				return authentication.equals(UsernamePasswordAuthenticationToken.class);
			}

			@Override
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				String username = authentication.getName();
				String password = authentication.getCredentials().toString();

				if (validaUsuario(username, password)) {
					return new UsernamePasswordAuthenticationToken(username, encoder().encode(password), Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
				} else {
					throw new BadCredentialsException("Invalid username/password");
				}
			}
		}).userDetailsService(new UserDetailsService(){
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				return new UserDetailsCustom(username, encoder().encode(""), Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
			}
		});
	}

	private boolean validaUsuario(String username, String password) {
		boolean respuesta = false;

		try {
			String host = Propiedades.getInstance().getProperty("user.api");
			URL url = new URL(host + "/usuario");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("usuario", username);
			conn.setRequestProperty("password", password);

//			String input = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
//
//			OutputStream os = conn.getOutputStream();
//			os.write(input.getBytes());
//			os.flush();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				respuesta = true;
			} else {
				respuesta = false;
			}

			conn.disconnect();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}

		return respuesta;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.csrf().disable().anonymous().disable().authorizeRequests().antMatchers("/oauth/token").permitAll();
//		http.headers().frameOptions().disable()
		http.csrf().disable()
		.authorizeRequests()
		.antMatchers("/oauth/token").permitAll()
		.antMatchers(HttpMethod.GET, "/props/**").permitAll()
		.antMatchers(HttpMethod.POST, "/props/**").anonymous()
//		.antMatchers(HttpMethod.POST, "/").permitAll()
//		.antMatchers(HttpMethod.GET, "/").permitAll()
//		.antMatchers(HttpMethod.PUT, "/").permitAll()
//		.antMatchers(HttpMethod.DELETE, "/**").permitAll()
//		.antMatchers(HttpMethod.OPTIONS, "*").permitAll()
		.anyRequest().authenticated().and().cors().configurationSource(corsConfigurationSource());
	}

	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(Collections.singletonList("*")); // <-- you may change "*"
	    configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
	    configuration.setAllowCredentials(true);
	    configuration.setAllowedHeaders(Arrays.asList(
	            "Accept", "Origin", "Content-Type", "Depth", "User-Agent", "If-Modified-Since,",
	            "Cache-Control", "Authorization", "X-Req", "X-File-Size", "X-Requested-With", "X-File-Name"));
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
	    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
	    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
	    return bean;
	}
}
