package payment_service.config;


import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public  class ModelMapperConfig  {


    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

//        modelMapper.typeMap(UpdateCartReqDto.class, CartItem.class)
//                .addMappings(mapper -> mapper.skip(CartItem::setCartItemId));

        modelMapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull());
        return modelMapper;

    }


}
