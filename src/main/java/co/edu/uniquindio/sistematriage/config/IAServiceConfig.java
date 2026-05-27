package co.edu.uniquindio.sistematriage.config;

import co.edu.uniquindio.sistematriage.repository.SolicitudRepository;
import co.edu.uniquindio.sistematriage.services.IAService;
import co.edu.uniquindio.sistematriage.services.IAServiceFallbackImpl;
import co.edu.uniquindio.sistematriage.services.IAServiceOpenAIImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración condicional del servicio de IA — patrón Factory Method.
 *
 * Según la propiedad {@code ia.provider}:
 *   - "openai"  → usa Spring AI + ChatClient
 *   - cualquier otro valor (o ausente) → usa el fallback basado en reglas (sin red)
 *
 * Esto garantiza RF-11: el sistema funciona sin IA si no hay API key configurada.
 */

// NOTA: IAServiceFallbackImpl e IAServiceOpenAIImpl se registran como @Bean
// desde esta clase de configuración en lugar de usar @Service directamente,
// porque la selección entre implementaciones depende de la propiedad
// ia.provider en application.properties (patrón Factory con @Conditional).
// Agregar @Service causaría doble registro y fallaría la inyección de ChatClient

@Slf4j
@Configuration
public class IAServiceConfig {

    @Value("${ia.provider:fallback}")
    private String iaProvider;

    @Bean
    public IAService iaService(SolicitudRepository solicitudRepository,
                               ChatClient.Builder chatClientBuilder) {

        IAServiceFallbackImpl fallback = new IAServiceFallbackImpl(solicitudRepository);

        if ("openai".equalsIgnoreCase(iaProvider)) {
            try {
                ChatClient chatClient = chatClientBuilder.build();
                log.info("IA Provider: OpenAI (Spring AI). Fallback activo si falla.");
                return new IAServiceOpenAIImpl(chatClient, solicitudRepository, fallback);
            } catch (Exception e) {
                log.warn("No se pudo inicializar OpenAI: {}. Usando fallback.", e.getMessage());
                return fallback;
            }
        }

        log.info("IA Provider: FALLBACK (reglas). Para usar OpenAI, configura ia.provider=openai");
        return fallback;
    }
}