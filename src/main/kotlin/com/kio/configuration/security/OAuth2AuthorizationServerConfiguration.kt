@file:Suppress("DEPRECATION")

package com.kio.configuration.security

import com.kio.configuration.properties.OAuthConfigurationProperties
import com.kio.shared.enums.OAuthGrantType
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
@EnableAuthorizationServer
class OAuth2AuthorizationServerConfiguration(
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val oAuthConfigurationProperties: OAuthConfigurationProperties
) : AuthorizationServerConfigurerAdapter() {

    @Bean
    fun tokenStore(): TokenStore{
        return InMemoryTokenStore()
    }

    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security.passwordEncoder(passwordEncoder)
            .checkTokenAccess("permitAll()")
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
            .withClient(oAuthConfigurationProperties.id)
            .secret(passwordEncoder.encode(oAuthConfigurationProperties.secret))
            .authorities("USER")
            .resourceIds("my_resource_id")
            .scopes("read")
            .authorizedGrantTypes(
                OAuthGrantType.REFRESH_TOKEN.grant,
                OAuthGrantType.PASSWORD.grant,
                OAuthGrantType.AUTHORIZATION_CODE.grant,
                OAuthGrantType.IMPLICIT.grant
            )
            .accessTokenValiditySeconds(oAuthConfigurationProperties.accessTokenValidityTime)
            .refreshTokenValiditySeconds(oAuthConfigurationProperties.refreshTokenValidityTime)
            .redirectUris("http://localhost:8080/")
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        val glaze = User.builder()
            .username("glaze")
            .password(passwordEncoder.encode("pass"))
            .authorities("read")
            .build()

        endpoints.authenticationManager(authenticationManager)
            .userDetailsService(InMemoryUserDetailsManager(glaze))
            .tokenStore(tokenStore())
    }
}