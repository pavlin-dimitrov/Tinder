package com.volasoftware.tinder.seeder;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Location;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.repository.LocationRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendSeeder implements CommandLineRunner {

  @Value("${run.commandlinerunner}")
  private boolean runCommandLineRunner;
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;
  private final LocationRepository locationRepository;
  private static final String LINK_PREFIX = "https://drive.google.com/file/d/";
  private static final String LINK_SUFFIX = "/view?usp=share_link";

  @Override
  public void run(String... args) {
    if (accountRepository.findAllByType(AccountType.BOT).isEmpty()) {
      runCommandLineRunner = true;
      List<String> firstNames =
          Arrays.asList(
              "John", "Jane", "Jack", "Jill", "James", "Jessica", "Michael", "Sarah", "David",
              "Emily");
      List<String> lastNames =
          Arrays.asList("Doe", "Smith", "Johnson", "Brown", "Miller", "Williams", "Jones", "Davis",
              "Wilson", "Taylor");
      Random random = new Random();

      for (int i = 0; i < 20; i++) {
        String firstName = firstNames.get(random.nextInt(firstNames.size()));
        String lastName = lastNames.get(random.nextInt(lastNames.size()));
        String image = LINK_PREFIX + getImageName().get(i) + LINK_SUFFIX;
        Gender gender = Gender.values()[random.nextInt(Gender.values().length)];
        int age = random.nextInt(40) + 20;

        Account account = new Account();
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setEmail(firstName + "." + lastName + i + "@example.com");
        account.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        account.setVerified(true);
        account.setRole(Role.USER);
        account.setImage(image);
        account.setGender(gender);
        account.setAge(age);
        account.setType(AccountType.BOT);
        account = accountRepository.save(account);

        Location location = new Location();
        location.setAccount(account);
        location.setLatitude(43.40 + random.nextDouble());
        location.setLongitude(23.21 + random.nextDouble());
        locationRepository.save(location);

        account.setLocation(location);
        accountRepository.save(account);
      }
    }
  }

  private List<String> getImageName() {
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
