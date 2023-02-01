package com.volasoftware.tinder.seeder;

import com.volasoftware.tinder.DTO.FriendDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Location;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.repository.LocationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendSeeder implements CommandLineRunner {

  private final AccountRepository accountRepository;

  private final ModelMapper modelMapper;

  private final LocationRepository locationRepository;

  private final static String LINK_PREFIX = "https://drive.google.com/file/d/";
  private final static String LINK_SUFFIX = "/view?usp=share_link";

  private List<FriendDTO> friends = new ArrayList<>();

  private Random random = new Random();

  @Override
  public void run(String... args) throws Exception {
    createFriends();
    accountRepository.saveAll(getAccounts());
  }

  private void createFriends() {
    for (int i = 0; i < 20; i++) {
      FriendDTO friend = new FriendDTO();
      friend.setFirstName("Friend" + i);
      friend.setLastName("Last" + i);
      friend.setImage(LINK_PREFIX + image().get(i) + LINK_SUFFIX);
      friend.setGender(Gender.values()[random.nextInt(Gender.values().length)]);
      friend.setAge(random.nextInt(50) + 18);

      Location location = new Location();
      location.setLatitude(random.nextDouble() + 43.21);
      location.setLongitude(random.nextDouble() + 23.34);
      locationRepository.save(location);
      friend.setLocation(location);

      friends.add(friend);
    }
  }

  private List<Account> getAccounts() {
    List<Account> accounts = new ArrayList<>();
    for (FriendDTO friend : friends) {
      Account account = modelMapper.map(friend, Account.class);
      account.setFirstName(friend.getFirstName());
      account.setLastName(friend.getLastName());
      account.setImage(friend.getImage());
      account.setGender(friend.getGender());
      account.setAge(friend.getAge());
      account.setLocation(friend.getLocation());
      accounts.add(account);
    }
    return accounts;
  }

  private List<String> image(){
    List<String> images = new ArrayList<>();
    images.add("1-zh9GCLyY6lVGzdqUYQXCEq157wlGv-l");
    images.add("1Q2m6ddQsBCLh57T1T5kbQskjC4VonsPa");
    images.add("18Um-gWbGWoNzWDxt--SEXHNDCRR1SRiA");
    images.add("1OIrj-dIxg_MtNXHgDhxxCh5QR8k_HQnr");
    images.add("1ffgPezeHftu8KnbMpajIhHT-uh1ax8Px");
    images.add("1hMROcce8u4gHvtyNmZ3gTiqbcZ4Iw23H");
    images.add("13BsokuPmkskp2feO0uWeCjukdE7pZppE");
    images.add("1sVmAFmeWnWFUVw5OMMM8oSY1lVUrG5gj");
    images.add("11g82ZFSoA1j5-0BDCivDgTr4NsghIgBY");
    images.add("1VurJl-kNLLkCQKsxnOwzKG2v2IvBfnGq");
    images.add("1xzKN0mZSQk8y0brEBR-GCOtMbuMpTBN9");
    images.add("1pJZQWqOjgE1UD-UxQLiQciB0D9ffQ-H1");
    images.add("1S6PRmHGsAcgZ67UYPZqAf5VCWIDhnzdu");
    images.add("1gOo4SyREOZLt99kKRCRHhlD7v0s4Suun");
    images.add("1DlMVdpggmi5k8aQGnwGppwjMWKUeIY8N");
    images.add("1qW_X5ZJtyBRoB0AHQ8dXeb93nst7Wv99");
    images.add("1C15-rUhSL6l3XNpAx5Ctf3ncVqiCf-iX");
    images.add("1B-Z8_bChkU9bZ6pEH6-Q9gk4cjeYMjMl");
    images.add("1oOctwFAbk9Atwq-7iEi7pzLJr6iDFrVx");
    images.add("1s6-isXbsegizTrjkwdHHLmcCyWguhiuD");
    return images;
  }
}
