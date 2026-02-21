package menucard_service.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

//        modelMapper.typeMap(RestaurantDiscountReqDto.class, RestaurantDiscount.class)
//                .addMappings(mapper -> mapper.skip(RestaurantDiscount::setId));

        modelMapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull());
        return modelMapper;
    }

}
