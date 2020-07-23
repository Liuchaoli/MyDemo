package com.iunin.cloud.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")//默认为 denyAll 表示拒绝所有  这里开放端点为申请令牌的端点 应当为所有人都可以访问
                .checkTokenAccess("isAuthenticated()")//默认也是为denyAll 表示拒绝所有  校验 令牌端点 应当是 只有登录之后才能校验
                .passwordEncoder(passwordEncoder);//设置密码需要使用加密器 针对客户端
        //.allowFormAuthenticationForClients();
    }

    //设置客户端配置 一定要配置，不配置就会报错 标识客户端配置项 ，
    // 支持 哪些授权模式 这里指定为changgou客户端可以有哪些授权模式
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //内存配置
        /*clients.inMemory()
                .withClient("changgou")//客户端ID
                .secret(passwordEncoder.encode("changgou"))//客户端秘钥 注意需要加密存储
                .authorizedGrantTypes(
                        "authorization_code",//授权码模式
                        "refresh_token",//刷新令牌
                        "password",//密码认证
                        "client_credentials" //客户端认证
                )
                .redirectUris("http://localhost")
                .refreshTokenValiditySeconds(3600)
                .accessTokenValiditySeconds(3600)
                .scopes("app");*/
        //数据库方式配置客户端
        clients.jdbc(dataSource).clients(jdbcClientDetailsService);

    }

    @Autowired
    private ClientDetailsService jdbcClientDetailsService;

    //设置数据库的方式的客户端
    @Bean
    public ClientDetailsService jdbcClientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Autowired
    private UserDetailsService userDetailsServiceImpl;

    @Autowired
    private RedisConnectionFactory tokenStore;

    @Bean
    public TokenStore tokenStore(){
        return new RedisTokenStore(tokenStore);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsServiceImpl)//一定要设置
                .tokenStore(tokenStore());//可以不设置，不设置会默认使用jwttoken 前提是使用了jwtAccessTokenConverter
                //设置converter 设置为jwt的令牌生成方式
    }
}
