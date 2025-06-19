package com.api.service;

import com.api.dto.response.FoodTypeResponse;
import com.api.entity.FoodType;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.FoodTypeRepository;
import com.api.service.Imp.FoodTypeServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodTypeServiceTest {

    @Mock
    private FoodTypeRepository foodTypeRepository;

    @InjectMocks
    private FoodTypeServiceImp foodTypeService;

    private FoodType testFoodType;

    @BeforeEach
    void setUp() {
        testFoodType = FoodType.builder()
                .name("Test Food Type")
                .build();
        testFoodType.setId(1L);
    }

    @Test
    void addNewFoodType_Success() {
        // Arrange
        String foodTypeName = "New Food Type";
        when(foodTypeRepository.existsByName(foodTypeName)).thenReturn(false);
        when(foodTypeRepository.save(any(FoodType.class))).thenAnswer(invocation -> {
            FoodType savedFoodType = invocation.getArgument(0);
            savedFoodType.setId(1L);
            return savedFoodType;
        });

        // Act
        long foodTypeId = foodTypeService.addNewFoodType(foodTypeName);

        // Assert
        assertEquals(1L, foodTypeId);

        ArgumentCaptor<FoodType> foodTypeCaptor = ArgumentCaptor.forClass(FoodType.class);
        verify(foodTypeRepository).save(foodTypeCaptor.capture());
        assertEquals(foodTypeName, foodTypeCaptor.getValue().getName());
    }

    @Test
    void addNewFoodType_DuplicateName_ThrowsException() {
        // Arrange
        String foodTypeName = "Existing Food Type";
        when(foodTypeRepository.existsByName(foodTypeName)).thenReturn(true);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            foodTypeService.addNewFoodType(foodTypeName);
        });

        assertEquals(ErrorCode.FOODTYPE_NAME_EXISTED, exception.getErrorCode());
        verify(foodTypeRepository, never()).save(any(FoodType.class));
    }

    @Test
    void getFoodTypeByName_ExistingName_ReturnsType() {
        // Arrange
        String foodTypeName = "Test Food Type";
        when(foodTypeRepository.findByName(foodTypeName)).thenReturn(Optional.of(testFoodType));

        // Act
        FoodType result = foodTypeService.getFoodTypeByName(foodTypeName);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(foodTypeName, result.getName());
    }

    @Test
    void getFoodTypeByName_NonExistingName_ThrowsException() {
        // Arrange
        String nonExistingName = "Non Existing Type";
        when(foodTypeRepository.findByName(nonExistingName)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            foodTypeService.getFoodTypeByName(nonExistingName);
        });

        assertEquals(ErrorCode.FOODTYPE_NAME_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void getAllFoodTypes_ReturnsAllTypes() {
        // Arrange
        FoodType type1 = FoodType.builder().name("Type 1").build();
        type1.setId(1L);

        FoodType type2 = FoodType.builder().name("Type 2").build();
        type2.setId(2L);

        List<FoodType> foodTypes = Arrays.asList(type1, type2);

        when(foodTypeRepository.findAll()).thenReturn(foodTypes);

        // Act
        List<FoodTypeResponse> responses = foodTypeService.getAllFoodTypes();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Type 1", responses.get(0).getName());
        assertEquals("Type 2", responses.get(1).getName());
    }

    @Test
    void getAllFoodTypes_NoTypes_ReturnsEmptyList() {
        // Arrange
        when(foodTypeRepository.findAll()).thenReturn(List.of());

        // Act
        List<FoodTypeResponse> responses = foodTypeService.getAllFoodTypes();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }
}