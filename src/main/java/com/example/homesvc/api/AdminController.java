package com.example.homesvc.api;

import com.example.homesvc.domain.enums.Region;
import com.example.homesvc.domain.enums.ServiceType;
import com.example.homesvc.domain.mongo.Booking;
import com.example.homesvc.domain.mongo.Provider;
import com.example.homesvc.domain.mongo.User;
import com.example.homesvc.infra.mongo.SequenceService;
import com.example.homesvc.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  private final UserRepo users;
  private final ProviderRepo providers;
  private final BookingRepo bookings;
  private final SequenceService seq;
  private static final Logger log = LoggerFactory.getLogger(AdminController.class);
  public AdminController(UserRepo users, ProviderRepo providers,
                         BookingRepo bookings, SequenceService seq){
    this.users=users;
    this.providers=providers;
    this.bookings=bookings;
    this.seq = seq;
  }
  @PostMapping("/seed")
  public String seed(){
    /*var uid = new AtomicLong(1);
    users.save(new User(uid.get(), "Alice", UserTier.REGULAR, Region.NORTH));
    users.save(new User(uid.incrementAndGet(), "Bob", UserTier.GOLD, Region.SOUTH));
    users.save(new User(uid.incrementAndGet(), "Carol", UserTier.PLATINUM, Region.EAST));
    providers.save(new Provider(10L, "PlumbMaster", Region.NORTH, java.util.EnumSet.of(ServiceType.PLUMBING), new BigDecimal("75"), true, 80));
    providers.save(new Provider(11L, "SparkPro", Region.NORTH, java.util.EnumSet.of(ServiceType.ELECTRICAL), new BigDecimal("85"), true, 70));
    providers.save(new Provider(12L, "HVACo", Region.EAST, java.util.EnumSet.of(ServiceType.HVAC), new BigDecimal("95"), true, 90));
    providers.save(new Provider(13L, "FixItAll", Region.SOUTH, java.util.EnumSet.of(ServiceType.PLUMBING, ServiceType.APPLIANCE), new BigDecimal("60"), false, 60));
    providers.save(new Provider(14L, "ApplianceGurus", Region.WEST, java.util.EnumSet.of(ServiceType.APPLIANCE), new BigDecimal("65"), true, 75));
    providers.save(new Provider(15L, "WireWizards", Region.NORTH, java.util.EnumSet.of(ServiceType.ELECTRICAL), new BigDecimal("78"), true, 88));
    return "seeded";*/

    long p1 = seq.next("provider");
    Provider a = new Provider();
    //a.id = p1;
    a.setName("PlumbMaster");
    a.region = Region.NORTH;
    a.licensed = true;
    a.hourlyRate = new BigDecimal("70");
    a.reputation = 95;
    a.skills = EnumSet.of(ServiceType.PLUMBING, ServiceType.HVAC);
    providers.save(a);
    log.info("provider created : " + a.id);
    return "seeded";
  }
  @GetMapping("/users")
  public List<User> users(){
    return users.findAll(); }
  @GetMapping("/providers")
  public List<Provider> providers(){
    return providers.findAll(); }
  @GetMapping("/bookings")
  public List<Booking> bookings(){
    return bookings.findAll(); }
}
