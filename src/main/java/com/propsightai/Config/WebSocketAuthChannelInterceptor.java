package com.propsightai.Config;

import com.propsightai.security.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private static final Logger logger =
            LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);


    @Autowired
    private com.propsightai.security.JwtService jwtService;


    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );


        if (accessor != null &&
                StompCommand.CONNECT.equals(accessor.getCommand())) {


            String auth =
                    accessor.getFirstNativeHeader("Authorization");


            if (auth != null &&
                    auth.startsWith("Bearer ")) {


                try {

                    String token =
                            auth.substring(7);


                    String email =
                            jwtService.extractEmail(token);


                    if (email != null) {


                        var userDetails =
                                userDetailsService
                                        .loadUserByUsername(email);


                        if (jwtService.isValid(
                                token,
                                userDetails
                        )) {


                            Principal principal =
                                    userDetails::getUsername;


                            accessor.setUser(principal);


                            logger.info(
                                    "WebSocket authenticated user: {}",
                                    email
                            );
                        }
                    }


                } catch (Exception e) {

                    logger.warn(
                            "WebSocket authentication failed: {}",
                            e.getMessage()
                    );
                }
            }
        }


        return message;
    }
}