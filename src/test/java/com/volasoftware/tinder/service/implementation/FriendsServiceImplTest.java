package com.volasoftware.tinder.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.FriendDTO;
import com.volasoftware.tinder.DTO.LocationDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Location;
import com.volasoftware.tinder.entity.Rating;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.exception.MissingFriendshipException;
import com.volasoftware.tinder.exception.OriginGreaterThenBoundException;
import com.volasoftware.tinder.mapper.FriendMapper;
import com.volasoftware.tinder.mapper.LocationMapper;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.repository.RatingRepository;
import com.volasoftware.tinder.service.contract.LocationService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class FriendsServiceImplTest {

  @Autowired FriendsServiceImpl underTest;
  @Mock private AccountRepository repository;
  @Mock private AccountServiceImpl service;
  @Mock private RatingRepository ratingRepository;
  @Mock private LocationService locationService;
  @Mock private FriendMapper friendMapper;
  private final static String location = "location";
  private final static String rating = "rating";
  private final static String asc = "asc";
  private final static String desc = "desc";
  private final static int limit = 2;

  @BeforeEach
  void setUp() {
    underTest = new FriendsServiceImpl(repository, ratingRepository, locationService, service, friendMapper);
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  @DisplayName("Get friend info by correct email and id")
  void testGetFriendInfoWhenCorrectEmailAndIdAreGivenThenExpectedUserInfo() {
    // given
    Account account =
        createAccount("John", "Doe", "john.doe@gmail.com", Gender.MALE, AccountType.REAL);
    Account friend1 =
        createAccount("Jane", "Doe", "jane.doe@gmail.com", Gender.FEMALE, AccountType.BOT);
    account.setFriends(setFriends(account, friend1));

    String userEmail = "john.doe@gmail.com";
    Long validFriendId = 2L;

    when(service.getAccountByEmailIfExists(userEmail)).thenReturn(account);
    when(service.getAccountByIdIfExists(validFriendId)).thenReturn(friend1);

    // when
    AccountDTO accountDTO = underTest.getFriendInfo(userEmail, validFriendId);

    // then
    assertNotNull(accountDTO);
    assertEquals("Jane", accountDTO.getFirstName());
    assertEquals("Doe", accountDTO.getLastName());
    assertEquals(Gender.FEMALE, accountDTO.getGender());
    assertThat(accountDTO.getEmail()).isEqualTo("jane.doe@gmail.com");
    verify(service, times(1)).getAccountByEmailIfExists(userEmail);
    verify(service, times(1)).getAccountByIdIfExists(validFriendId);
    verifyNoMoreInteractions(service);
  }

  @Test
  @DisplayName("Throw exception when user and friend are not friends")
  void testGetFriendInfoWhenInvalidFriendIdIsGivenThenThrowAnException() {
    // given
    Account account =
        createAccount("John", "Doe", "john.doe@gmail.com", Gender.MALE, AccountType.REAL);
    Account friend1 =
        createAccount("Jane", "Doe", "jane.doe@gmail.com", Gender.FEMALE, AccountType.REAL);
    Account friend2 =
        createAccount("Bob", "Saab", "bob.saab@gmail.com", Gender.MALE, AccountType.BOT);
    account.setFriends(setFriends(account, friend1));
    String userEmail = "john.doe@gmail.com";
    Long invalidFriendId = 3L;

    when(service.getAccountByEmailIfExists(userEmail)).thenReturn(account);
    when(service.getAccountByIdIfExists(invalidFriendId)).thenReturn(friend2);
    // when and then
    Assertions.assertThatThrownBy(() -> underTest.getFriendInfo(userEmail, invalidFriendId))
        .isInstanceOf(MissingFriendshipException.class)
        .hasMessageContaining("You are not friend with this user!");
  }

  @Test
  @DisplayName("Seed random friends for all real accounts")
  void testLinkingAllRealAccountsWithRandomFriendsWhenCorrectDataIsGivenThenExpectNotEmptyFriendsSets() {
    //given
    Account account1 = createAccount("John", "Doe", "john.doe@gmail.com", Gender.MALE, AccountType.REAL);
    account1.setId(1L);
    Account account2 = createAccount("Jane", "Doe", "jane.doe@gmail.com", Gender.FEMALE, AccountType.REAL);
    account2.setId(2L);
    Account account3 = createAccount("Bob", "Saab", "bob.saab@gmail.com", Gender.MALE, AccountType.BOT);
    account3.setId(3L);
    Account account4 = createAccount("Jamie","Johns","jamie.johns@gmail.com", Gender.MALE, AccountType.BOT);
    account4.setId(4L);
    Account account5 = createAccount("J","Smith","j.smith@gmail.com", Gender.MALE, AccountType.BOT);
    account5.setId(5L);
    List<Account> allAccounts = List.of(account1, account2, account3, account4, account5);
    repository.saveAll(allAccounts);

    when(repository.findAll()).thenReturn(allAccounts);
    when(repository.findAllByType(AccountType.REAL)).thenReturn(List.of(account1, account2));
    when(repository.findAllByType(AccountType.BOT)).thenReturn(List.of(account3, account4, account5));
    // when
    ResponseDTO responseDTO = underTest.linkingAllRealAccountsWithRandomFriends();
    //then
    assertThat(responseDTO.getResponse()).isEqualTo("Friends seeded successfully!");
    List<Account> realAccounts = repository.findAllByType(AccountType.REAL);
    for (Account realAccount : realAccounts) {
      assertThat(realAccount.getFriends()).isNotNull();
    }
  }

  @Test
  @DisplayName("Throw exception when bound is less the origin")
  void testLinkingAllRealAccountsWithRandomFriendsWhenBoundIsLessThenOriginThenThrowAnException() {
    //given
    Account account1 = createAccount("John", "Doe", "john.doe@gmail.com", Gender.MALE, AccountType.REAL);
    account1.setId(1L);
    Account account2 = createAccount("Jane", "Doe", "jane.doe@gmail.com", Gender.FEMALE, AccountType.REAL);
    account2.setId(2L);
    List<Account> allAccounts = List.of(account1, account2);
    repository.saveAll(allAccounts);

    when(repository.findAll()).thenReturn(allAccounts);
    when(repository.findAllByType(AccountType.REAL)).thenReturn(List.of(account1, account2));
    //when and then
    Assertions.assertThatThrownBy(() -> underTest.linkingAllRealAccountsWithRandomFriends())
        .isInstanceOf(OriginGreaterThenBoundException.class)
        .hasMessageContaining("Bound must be greater then origin");
  }

  @Test
  @DisplayName("Seed random friends for requested real accounts")
  void linkingRequestedRealAccountWithRandomFriends() {
    //given
    Account requestedAccount = createAccount("John", "Doe", "john.doe@gmail.com", Gender.MALE, AccountType.REAL);
    requestedAccount.setId(1L);
    Account bot1 = createAccount("Bob", "Saab", "bob.saab@gmail.com", Gender.MALE, AccountType.BOT);
    bot1.setId(2L);
    Account bot2 = createAccount("Jamie","Johns","jamie.johns@gmail.com", Gender.MALE, AccountType.BOT);
    bot2.setId(3L);
    List<Account> allAccounts = List.of(requestedAccount, bot1, bot2);
    repository.saveAll(allAccounts);

    when(repository.findAll()).thenReturn(allAccounts);
    when(service.getAccountByIdIfExists(requestedAccount.getId())).thenReturn(requestedAccount);
    when(repository.findAllByType(AccountType.BOT)).thenReturn(List.of(bot1, bot2));
    //when
    ResponseDTO responseDTO = underTest.linkingRequestedRealAccountWithRandomFriends(requestedAccount.getId());
    //then
    assertThat(responseDTO.getResponse()).isEqualTo("Friends seeded successfully!");
    assertThat(requestedAccount.getFriends()).isNotNull();
    assertThat(bot1.getFriends()).isNull();
    assertThat(bot2.getFriends()).isNull();
  }

  @Test
  @DisplayName("Seed random friends for requested NOT real account")
  void linkingRequestedNotRealAccountWithRandomFriendsExpectException() {
    //given
    Account requestedAccount = createAccount("John", "Doe", "john.doe@gmail.com", Gender.MALE, AccountType.BOT);
    requestedAccount.setId(1L);
    repository.saveAndFlush(requestedAccount);
    when(service.getAccountByIdIfExists(requestedAccount.getId())).thenReturn(requestedAccount);
    //when
    ResponseDTO responseDTO = underTest.linkingRequestedRealAccountWithRandomFriends(requestedAccount.getId());
    //then
    assertThat(responseDTO.getResponse()).isEqualTo("Seeding friends was not successful!");
  }

  @Test
  @DisplayName("Seed friends asynchronously")
  void linkFriendsAsync() {
    //given
    Account requestedAccount = createAccount("John", "Doe", "john.doe@gmail.com", Gender.MALE, AccountType.REAL);
    requestedAccount.setId(1L);
    Account bot1 = createAccount("Bob", "Saab", "bob.saab@gmail.com", Gender.MALE, AccountType.BOT);
    bot1.setId(2L);
    Account bot2 = createAccount("Jamie","Johns","jamie.johns@gmail.com", Gender.MALE, AccountType.BOT);
    bot2.setId(3L);
    List<Account> allAccounts = List.of(requestedAccount, bot1, bot2);
    repository.saveAll(allAccounts);

    when(repository.findAll()).thenReturn(allAccounts);
    when(service.getAccountByIdIfExists(requestedAccount.getId())).thenReturn(requestedAccount);
    when(repository.findAllByType(AccountType.BOT)).thenReturn(List.of(bot1, bot2));
    //when
    underTest.linkFriendsAsync(requestedAccount.getId());
    //then
    assertThat(requestedAccount.getFriends()).isNotNull();
    assertThat(bot1.getFriends()).isNull();
    assertThat(bot2.getFriends()).isNull();
  }

  @Test
  @DisplayName("Show filtered list of friends sorted by Location, ordered by Asc")
  void showFilteredListOfFriendsSortedByLocationOrderedByASC() {
    //given
    Account account = getAccounts().get(0);
    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn(account.getEmail());
    when(service.getAccountByEmailIfExists(principal.getName())).thenReturn(account);
    LocationDTO principalLocation = LocationMapper.INSTANCE.locationToLocationDTO(account.getLocation());
    //when
    List<FriendDTO> filteredList = underTest.showFilteredListOfFriends(location, asc, principal, principalLocation, limit);
    //then
    assertThat(filteredList.size()).isEqualTo(2);
    assertThat(filteredList.get(0).getFirstName()).isEqualTo("Mezdra");
    assertThat(filteredList.get(1).getFirstName()).isEqualTo("Sofia");
  }

  @Test
  @DisplayName("Show filtered list of friends sorted by Location, ordered by DESC")
  void showFilteredListOfFriendsSortedByLocationOrderedByDESC() {
    //given
    Account account = getAccounts().get(0);
    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn(account.getEmail());
    when(service.getAccountByEmailIfExists(principal.getName())).thenReturn(account);
    LocationDTO principalLocation = LocationMapper.INSTANCE.locationToLocationDTO(account.getLocation());
    //when
    List<FriendDTO> filteredList = underTest.showFilteredListOfFriends(location, desc, principal, principalLocation, null);
    //then
    assertThat(filteredList.size()).isEqualTo(2);
    assertThat(filteredList.get(0).getFirstName()).isEqualTo("Sofia");
    assertThat(filteredList.get(1).getFirstName()).isEqualTo("Mezdra");
  }

  @Test
  @DisplayName("Show filtered list of friends sorted by Location, ordered by Default")
  void showFilteredListOfFriendsSortedByLocationOrderedByDefault() {
    //given
    Account account = getAccounts().get(0);
    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn(account.getEmail());
    when(service.getAccountByEmailIfExists(principal.getName())).thenReturn(account);
    LocationDTO principalLocation = LocationMapper.INSTANCE.locationToLocationDTO(account.getLocation());
    //when
    List<FriendDTO> filteredList = underTest.showFilteredListOfFriends(location, null, principal, principalLocation, null);
    //then
    assertThat(filteredList.size()).isEqualTo(2);
    assertThat(filteredList.get(0).getFirstName()).isEqualTo("Sofia");
    assertThat(filteredList.get(1).getFirstName()).isEqualTo("Mezdra");
  }

  @Test
  @DisplayName("Show filtered list of friends sorted by Rating, ordered by Asc")
  void showFilteredListOfFriendsSortedByRatingOrderedByASC() {
    //given
    Account account = getAccounts().get(0);
    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn(account.getEmail());
    when(service.getAccountByEmailIfExists(principal.getName())).thenReturn(account);
    when(ratingRepository.findAllByAccount(account)).thenReturn(getRatings());
    LocationDTO principalLocation = LocationMapper.INSTANCE.locationToLocationDTO(account.getLocation());
    //when
    List<FriendDTO> filteredList = underTest.showFilteredListOfFriends(rating, asc, principal, principalLocation, null);
    //then
    assertThat(filteredList.get(0).getFirstName()).isEqualTo("Mezdra");
    assertThat(filteredList.get(1).getFirstName()).isEqualTo("Sofia");
  }

  @Test
  @DisplayName("Show filtered list of friends sorted by Rating, ordered by DESC")
  void showFilteredListOfFriendsSortedByRatingOrderedByDESC() {
    //given
    Account account = getAccounts().get(0);
    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn(account.getEmail());
    when(service.getAccountByEmailIfExists(principal.getName())).thenReturn(account);
    when(ratingRepository.findAllByAccount(account)).thenReturn(getRatings());
    LocationDTO principalLocation = LocationMapper.INSTANCE.locationToLocationDTO(account.getLocation());
    //when
    List<FriendDTO> filteredList = underTest.showFilteredListOfFriends(rating, desc, principal, principalLocation, null);
    //then
    assertThat(filteredList.get(0).getFirstName()).isEqualTo("Sofia");
    assertThat(filteredList.get(1).getFirstName()).isEqualTo("Mezdra");
  }

  @Test
  @DisplayName("Show filtered list of friends sorted by Rating, ordered by Default")
  void showFilteredListOfFriendsSortedByRatingOrderedByDefault() {
    //given
    Account account = getAccounts().get(0);
    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn(account.getEmail());
    when(service.getAccountByEmailIfExists(principal.getName())).thenReturn(account);
    when(ratingRepository.findAllByAccount(account)).thenReturn(getRatings());
    LocationDTO principalLocation = LocationMapper.INSTANCE.locationToLocationDTO(account.getLocation());
    //when
    List<FriendDTO> filteredList = underTest.showFilteredListOfFriends(rating, null, principal, principalLocation, null);
    //then
    assertThat(filteredList.get(0).getFirstName()).isEqualTo("Sofia");
    assertThat(filteredList.get(1).getFirstName()).isEqualTo("Mezdra");
  }

  @Test
  @DisplayName("Show filtered list of friends sorted by Default, ordered by Asc")
  void showFilteredListOfFriendsSortedByDefaultOrderedByASC() {
    //given
    Account account = getAccounts().get(0);
    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn(account.getEmail());
    when(service.getAccountByEmailIfExists(principal.getName())).thenReturn(account);
    LocationDTO principalLocation = LocationMapper.INSTANCE.locationToLocationDTO(account.getLocation());
    //when
    List<FriendDTO> filteredList = underTest.showFilteredListOfFriends(null, asc, principal, principalLocation, limit);
    //then
    assertThat(filteredList.size()).isEqualTo(2);
    assertThat(filteredList.get(0).getFirstName()).isEqualTo("Mezdra");
    assertThat(filteredList.get(1).getFirstName()).isEqualTo("Sofia");
  }

  @Test
  @DisplayName("Show filtered list of friends sorted by Default, ordered by DESC")
  void showFilteredListOfFriendsSortedByDefaultOrderedByDESC() {
    //given
    Account account = getAccounts().get(0);
    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn(account.getEmail());
    when(service.getAccountByEmailIfExists(principal.getName())).thenReturn(account);
    LocationDTO principalLocation = LocationMapper.INSTANCE.locationToLocationDTO(account.getLocation());
    //when
    List<FriendDTO> filteredList = underTest.showFilteredListOfFriends(null, desc, principal, principalLocation, null);
    //then
    assertThat(filteredList.size()).isEqualTo(2);
    assertThat(filteredList.get(0).getFirstName()).isEqualTo("Sofia");
    assertThat(filteredList.get(1).getFirstName()).isEqualTo("Mezdra");
  }

  @Test
  void testIfAccount1IsFriendWithAccount2(){
    //given
    List<Account> accounts = getAccounts();
    Account account1 = accounts.get(0);
    Account account2 = accounts.get(1);

    if (account1.getFriends().contains(account2)){
      String friendName = account2.getEmail();
      assertThat(friendName).isEqualTo("jane.care@mail.com");
    }
  }

  private Set<Account> setFriends(Account account, Account friend) {
    Set<Account> friends = new HashSet<>();
    friends.add(account);
    friends.add(friend);
    account.setFriends(friends);
    friend.setFriends(friends);
    return friends;
  }

  private Account createAccount(
      String firstName, String lastName, String email, Gender gender, AccountType type) {
    Account account = new Account();
    account.setFirstName(firstName);
    account.setLastName(lastName);
    account.setEmail(email);
    account.setPassword("password");
    account.setGender(gender);
    account.setRole(Role.USER);
    account.setType(type);
    return account;
  }

  private List<Account> getAccounts(){
    List<Account> accounts = new ArrayList<>();
    Account account1 = Account.builder().id(1L).firstName("Vratsa").email("john.doe@gmail.com").build();
    Account account2 = Account.builder().id(2L).firstName("Mezdra").email("jane.care@mail.com").build();
    Account account3 = Account.builder().id(3L).firstName("Sofia").email("bob.davide@gmail.com").build();

    accounts.add(account1);
    accounts.add(account2);
    accounts.add(account3);

    Location location1 = Location.builder().id(1L).account(account1).latitude(43.200328).longitude(23.553883).build();
    Location location2 = Location.builder().id(2L).account(account2).latitude(43.142382).longitude(23.707429).build();
    Location location3 = Location.builder().id(3L).account(account3).latitude(42.652041).longitude(23.347395).build();

    account1.setLocation(location1);
    account2.setLocation(location2);
    account3.setLocation(location3);

    Set<Account> account1Friends = new HashSet<>();
    account1Friends.add(account2);
    account1Friends.add(account3);
    account1.setFriends(account1Friends);

    Set<Account> account2Friends = new HashSet<>();
    account2Friends.add(account3);
    account2.setFriends(account2Friends);

    return accounts;
  }

  private List<Rating> getRatings(){
    List<Account> accounts = getAccounts();
    List<Rating> ratings = new ArrayList<>();
    Rating rating1 = Rating.builder().id(1L).account(accounts.get(0)).friend(accounts.get(1)).rating(2).build();
    Rating rating2 = Rating.builder().id(2L).account(accounts.get(0)).friend(accounts.get(2)).rating(3).build();
    ratings.add(rating1);
    ratings.add(rating2);
    return ratings;
  }
}
