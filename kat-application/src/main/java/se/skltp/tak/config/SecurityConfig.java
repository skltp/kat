package se.skltp.tak.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${cxf.path}")
  private String cxfPath;

  @Value("${kat.resetcache.path}")
  private String resetCachePath;

  @SuppressWarnings("java:S4502") // CSRF not applicable: stateless, no cookies/session, no browser UI
  @Bean
  @Order(1)
  SecurityFilterChain actuatorFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher(EndpointRequest.toAnyEndpoint()) // Matches all actuator endpoints
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(EndpointRequest.to(HealthEndpoint.class, PrometheusScrapeEndpoint.class)).permitAll()
            .anyRequest().denyAll())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  @SuppressWarnings("java:S4502") // CSRF not applicable: stateless system-to-system SOAP, no cookies/session
  @Bean
  @Order(2)
  SecurityFilterChain appFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, cxfPath + "/**").permitAll()
            .requestMatchers(HttpMethod.GET, cxfPath + "/**").permitAll()
            .requestMatchers(HttpMethod.GET, resetCachePath).permitAll()
            .anyRequest().denyAll())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
