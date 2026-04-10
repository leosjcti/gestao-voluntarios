package br.com.ibaji.voluntarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        // 🔥 ALTERAÇÃO 1: Atualizamos a exceção do CSRF para a nova rota onde o formulário faz o POST
                        .csrf(csrf -> csrf.ignoringRequestMatchers("/salvar"))
                        .authorizeHttpRequests((requests) -> requests
                                // 🔥 ALTERAÇÃO 2: Trocamos "/voluntarios/**" pelas rotas limpas
                                .requestMatchers("/", "/cadastro", "/salvar", "/sucesso", "/css/**", "/js/**", "/images/**")
                                .permitAll()

                                .requestMatchers("/admin/usuarios/**").hasRole("SUPER_ADMIN")
                                .requestMatchers("/admin/voluntarios/novo",
                                        "/admin/voluntarios/editar/**",
                                        "/admin/voluntarios/salvar",
                                        "/admin/voluntarios/deletar/**")
                                .hasRole("SUPER_ADMIN")
                                .requestMatchers("/admin/bases/novo", "/admin/bases/editar/**",
                                        "/admin/bases/salvar", "/admin/bases/deletar/**")
                                .hasRole("SUPER_ADMIN")
                                .requestMatchers("/admin/ministerios/novo",
                                        "/admin/ministerios/editar/**",
                                        "/admin/ministerios/salvar",
                                        "/admin/ministerios/deletar/**")
                                .hasRole("SUPER_ADMIN")

                                // Qualquer outra coisa exige login
                                .anyRequest().authenticated())
                        .formLogin((form) -> form
                                .loginPage("/login")
                                .permitAll()
                                .defaultSuccessUrl("/admin", true))
                        .logout((logout) -> logout
                                .logoutSuccessUrl("/login?logout") // Volta para o login com mensagem de
                                // saída
                                .permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // @Bean
        // public UserDetailsService userDetailsService() {
        // UserDetails admin = User.withDefaultPasswordEncoder()
        // .username("admin")
        // .password("admin123")
        // .roles("ADMIN")
        // .build();
        //
        // return new InMemoryUserDetailsManager(admin);
        // }
}