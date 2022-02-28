package com.breakbooking.eventbookingapi.config;


import com.breakbooking.eventbookingapi.common.BookingRequest;

import com.breakbooking.eventbookingapi.common.PaymentRequest;
import com.breakbooking.eventbookingapi.model.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;


@Configuration
public class KafkaConfiguration {

    /*
    @Bean
    public ProducerFactory<String, String> producerFactoryString(){
        Map<String,Object> config=new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);


        return new DefaultKafkaProducerFactory<String,String>(config);

    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString(){
        return new KafkaTemplate<String, String>(producerFactoryString());
    }*/

    /* @Bean
    public ProducerFactory<String, Payment> producerFactoryPayment(){
        Map<String,Object> config=new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS,false);

        return new DefaultKafkaProducerFactory<String,Payment>(config);

    }

    @Bean
    public KafkaTemplate<String, Payment> kafkaTemplatePayment(){
        return new KafkaTemplate<String, Payment>(producerFactoryPayment());
    }*/

/*
    @Bean
    public ProducerFactory<String, Booking> pf1(){
        Map<String,Object> config=new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

//        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS,false);

        return new DefaultKafkaProducerFactory<String,Booking>(config);

    }
   @Bean
   public ProducerFactory<String, Payment> pf2(){
       Map<String,Object> config=new HashMap<>();
       config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
       config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
       config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

//       config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS,false);

       return new DefaultKafkaProducerFactory<String,Payment>(config);

   }

/*
     @Bean
    public ProducerFactory<String, BookingRequest> producerFactoryEmail(){
        Map<String,Object> config=new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

//        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS,false);

        return new DefaultKafkaProducerFactory<String,BookingRequest>(config);

    }

    @Bean
    public KafkaTemplate<String, BookingRequest> EmailkafkaTemplate(){
        return new KafkaTemplate<String, BookingRequest>(producerFactoryEmail());
    }

*/


    //kafka template for payment

    @Value("${kafka.group.id}")
    private String groupId;
    @Value("${kafka.reply.topic}")
    private String replyTopic;

    @Bean
    public ReplyingKafkaTemplate<String, PaymentRequest, Payment> replyingKafkaTemplate(ProducerFactory<String, PaymentRequest> pf,
                                                                                        ConcurrentKafkaListenerContainerFactory<String, Payment> factory) {
        ConcurrentMessageListenerContainer<String, Payment> replyContainer = factory.createContainer(replyTopic);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(groupId);

        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public KafkaTemplate<String, Payment> replyTemplate(ProducerFactory<String, Payment> pf,
                                                        ConcurrentKafkaListenerContainerFactory<String, Payment> factory) {
        KafkaTemplate<String, Payment> kafkaTemplate = new KafkaTemplate<>(pf);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.setReplyTemplate(kafkaTemplate);
        return kafkaTemplate;

    }

    //kafka template for email

    @Bean
    public KafkaTemplate<String, BookingRequest> kafkaTemplate(ProducerFactory<String, BookingRequest> pf,
                                                               ConcurrentKafkaListenerContainerFactory<String, BookingRequest> factory) {
        KafkaTemplate<String, BookingRequest> kafkaTemplate = new KafkaTemplate<>(pf);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.setReplyTemplate(kafkaTemplate);
        return kafkaTemplate;

    }

    //kafka template for string

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString(ProducerFactory<String, String> pf,
                                                             ConcurrentKafkaListenerContainerFactory<String, String> factory) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(pf);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.setReplyTemplate(kafkaTemplate);
        return kafkaTemplate;

    }

}
