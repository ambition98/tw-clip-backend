//package com.isedol_clip_backend.config;
//
//import org.modelmapper.ModelMapper;
//import org.modelmapper.convention.MatchingStrategies;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//@Configuration
//public class ModelMapperConfig {
//    private final ModelMapper modelMapper = new ModelMapper();
//
//    @Bean
//    @Primary
//    public ModelMapper modelMapper() {
//        modelMapper.getConfiguration()
//                .setMatchingStrategy(MatchingStrategies.STRICT)
//                .setSkipNullEnabled(true);
//
//        return modelMapper;
//    }
//
//    @Bean
//    public ModelMapper jsonMapper() {
//        modelMapper.getConfiguration()
//                .setSkipNullEnabled(true)
//                .addValueReader(new JsonNodeValueReader());
//
//        return modelMapper;
//    }
//}
