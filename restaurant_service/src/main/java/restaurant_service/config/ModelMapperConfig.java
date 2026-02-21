package restaurant_service.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import restaurant_service.dto.RestaurantDiscountReqDto;
import restaurant_service.model.RestaurantDiscount;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(RestaurantDiscountReqDto.class, RestaurantDiscount.class)
                .addMappings(mapper -> mapper.skip(RestaurantDiscount::setId));

        modelMapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull());
        return modelMapper;
    }

}
