package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dto.FriendDto;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FriendMapperTest {

    private final FriendMapper friendMapper = FriendMapper.INSTANCE;

    @Test
    void accountToFriendDto() {
        // Given

        Account account = Account.builder()
                .id(1L)
                .firstName("John")
                .email("john.doe@gmail.com")
                .build();

        Location location = Location.builder().account(account).id(1L).latitude(33.45).longitude(34.54).build();

        account.setLocation(location);

        // When
        FriendDto friendDto = friendMapper.accountToFriendDto(account);

        // Then
        assertThat(friendDto).isNotNull();
        assertThat(friendDto.getLocationDto().getLatitude()).isEqualTo(account.getLocation().getLatitude());
        assertThat(friendDto.getFirstName()).isEqualTo(account.getFirstName());
    }
}