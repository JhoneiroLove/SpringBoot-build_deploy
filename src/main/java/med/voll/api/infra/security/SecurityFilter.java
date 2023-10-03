package med.voll.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.api.domain.usuarios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Obtener el token del header
        var authHeader = request.getHeader("Authorization");
        if(authHeader != null){
            var token = authHeader.replace("Bearer ", "");
            var nombreUsario = tokenService.getSubject(token);
            if (nombreUsario != null) {
                // Token valido
                var usuario = usuarioRepository.findByLogin(nombreUsario);
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null,
                        usuario.getAuthorities()); //forzamos un inicio de sesion
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
