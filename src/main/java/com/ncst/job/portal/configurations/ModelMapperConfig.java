package com.ncst.job.portal.configurations;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
     ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Converter: LocalDateTime -> String (ISO)
        Converter<LocalDateTime, String> ldtToString = new Converter<LocalDateTime, String>() {
            @Override
            public String convert(MappingContext<LocalDateTime, String> ctx) {
                LocalDateTime src = ctx.getSource();
                return src == null ? null : src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        };
        mapper.addConverter(ldtToString, LocalDateTime.class, String.class);

        // (Optional) configure strict matching or other mapping rules if needed
        // mapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);

        return mapper;
    }
}

