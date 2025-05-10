package com.api.service.Imp;

import com.api.dto.response.FoodTypeResponse;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.entity.FoodType;
import com.api.repository.FoodTypeRepository;
import com.api.service.FoodTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodTypeServiceImp implements FoodTypeService {
    private final FoodTypeRepository foodTypeRepository;

    @Override
    @Transactional
    public long addNewFoodType(String name) {
        log.info("add new FoodType");
        if (foodTypeRepository.existsByName(name)) {
            log.error("FoodType with name {} already exists", name);
            throw new AppException(ErrorCode.FOODTYPE_NAME_EXISTED);
        }
        return foodTypeRepository.save(FoodType.builder()
                .name(name)
                .build()).getId();
    }

    @Override
    public FoodType getFoodTypeByName(String name) {
        log.info("Get FoodType by name {}", name);
        return foodTypeRepository.findByName(name).orElseThrow( () -> {
            log.error("FoodType with name {} does not exist", name);
            return new AppException(ErrorCode.FOODTYPE_NAME_NOT_EXISTED);
        });
    }

    @Override
    public List<FoodTypeResponse> getAllFoodTypes() {
        return foodTypeRepository.findAll().stream()
                .map(ft ->
                        FoodTypeResponse.builder()
                                .name(ft.getName())
                                .build())
                .collect(Collectors.toList());
    }


}
