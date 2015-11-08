package org.pazu;

import org.pazu.security.AccountAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	private AccountAuthenticationProvider accountAuthenticationProvider;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(accountAuthenticationProvider);
	}

	@Configuration
	@Order(1)
	// Lower values have higher priority.
	public static class ApiWebSecurityConfigurerAdapter extends
			WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			// @formatter:off

			http.antMatcher("/api/**").authorizeRequests().anyRequest()
					.hasRole("USER").and().httpBasic().and()
					.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			// Naming convention : ROLE_XXXX
			// USER = ROLE_USER
			// ADMIN = ROLE_ADMIN
			// SYSADMIN = ROLE_SYSADMIN

			/*
			 * While there are several considerations when choosing to implement
			 * a Stateless or Stateful Spring Security solution, a few of the
			 * more important ones are performance and scalability.
			 * 
			 * 
			 * You are correct. The Stateless solution illustrated in this three
			 * part series will cause Spring Security to query the database for
			 * every authentication request. This is certainly less performant
			 * than a Stateful solution. (I'll explain why I chose Stateless
			 * over Stateful in just a moment.) There are ways to improve the
			 * performance of a Stateless Spring Security solution backed by a
			 * database, but it is unlikely that these will be more performant
			 * than the Stateful approach.
			 * 
			 * 
			 * You can use the @Cacheable family of annotations on the
			 * AccountService method(s) so that the results of the
			 * "findByUsername" method are cached for some reasonable, but
			 * short, amount of time. This approach should be discussed among
			 * the senior IT members and security staff to ensure that
			 * performance gains and security concerns are kept in balance. For
			 * example, you probably don't want to cache a successful response
			 * for a day, but some shorter period is more reasonable like 15 or
			 * 30 minutes. You should ensure that you use a distributed caching
			 * solution to support scaling your Stateless application across
			 * many clustered nodes. When possible, implement a "Sign Out" web
			 * service that will remove the cached authentication object for the
			 * user rather than waiting for the cached item to time out.
			 * 
			 * 
			 * In addition (or alternatively), ensure that the username column
			 * is indexed on the database table so that the query used by Spring
			 * Security is executed as quickly as possible by the database
			 * engine.
			 * 
			 * 
			 * Why did I choose to illustrate a Stateless solution instead of
			 * Stateful, which is how Spring Security operates out-of-the-box?
			 * The answer: experience and personal preference.
			 * 
			 * 
			 * In the last two years, I have the opportunity to see both
			 * solutions implemented in production applications. Both were web
			 * services applications (their user interfaces were Marionette and
			 * Backbone JS applications external to the Spring web services
			 * application.) Both applications had the traditional need for a
			 * user to authenticate to and retain an authenticated "session" for
			 * some period of time or until signing out.
			 * 
			 * 
			 * The first solution implemented a Stateless security model. The
			 * user details object was cached in a distributed cache and every
			 * call to a web service would re-authenticate, but used the cached
			 * UserDetails object.
			 * 
			 * 
			 * The second solution implemented a Stateful security model. It
			 * followed the very traditional approach and let Spring Security
			 * perform the Session Management work.
			 * 
			 * 
			 * The second solution was a bit easier for the application
			 * developers; however, it was more challenging operationally. As
			 * the Stateful solution grew and we needed to scale the web
			 * services component to many load balanced nodes, the operational
			 * complexity grew. We had to evaluate "sticky" routing or
			 * clustering our Apache Tomcat nodes together. Since our project
			 * team had had poor luck clustering Apache Tomcat (or any J2EE web
			 * server) in the past, we implemented "sticky" routing in the load
			 * balancer.
			 * 
			 * 
			 * From an operational perspective, the Stateless approach was far
			 * simpler. We could create as many nodes of the web services as
			 * were needed to service the standard transaction volume responding
			 * in N seconds. When peak periods occurred, our operations guys
			 * loved that it was easy to automate the creation new virtual nodes
			 * to horizontally scale the web services application.
			 * 
			 * 
			 * One final (philosophical) note. I don't subscribe to the belief
			 * that certain IT system solutions or approaches are the
			 * "right way" or the "wrong way". Each system has a unique set of
			 * functional and non-functional requirements that help designers
			 * and architects make the "best choice" for an IT system at a
			 * specific point in time. Often these functional and non-functional
			 * requirements change over time (or do not materialize in the first
			 * place) and decisions made on those assumed requirements must be
			 * reevaluated.
			 */
			// @formatter:on
		}

	}

	// @Configuration
	// @Order(2)
	// public static class ActuatorWebSecurityConfigurerAdapter extends
	// WebSecurityConfigurerAdapter {
	//
	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	//
	// // @formatter:off
	//
	// http.antMatcher("/actuators/**").authorizeRequests().anyRequest()
	// .hasRole("SYSADMIN").and().httpBasic().and()
	// .sessionManagement()
	// .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	//
	// // @formatter:on
	//
	// }
	//
	// }

}
