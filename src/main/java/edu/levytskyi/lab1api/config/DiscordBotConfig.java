package edu.levytskyi.lab1api.config;

import edu.levytskyi.lab1api.service.DiscordMessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:env.properties")
public class DiscordBotConfig {

  @Value("${DISCORD_BOT_TOKEN}")
  private String discordToken;

  @Bean
  public JDA jda(DiscordMessageListener listener) {
    return JDABuilder.createDefault(discordToken)
        // Активація дозволу на читання вмісту повідомлень (Message Content Intent)
        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
        .addEventListeners(listener)
        .build();
  }
}