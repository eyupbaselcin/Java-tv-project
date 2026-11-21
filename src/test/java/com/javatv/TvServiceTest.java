package com.javatv;

import com.javatv.controller.TvController;
import com.javatv.enums.ChannelCategory;
import com.javatv.exception.TvPoweredOffException;
import com.javatv.model.Channel;
import com.javatv.model.Television;
import com.javatv.repository.ChannelRepository;
import com.javatv.repository.InMemoryChannelRepository;
import com.javatv.service.TvService;
import com.javatv.service.TvServiceImpl;

/**
 * Sistem testlerini yürüten bağımsız test sınıfı.
 * Dış bağımlılık olmadan doğrudan çalıştırılabilir.
 */
public class TvServiceTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  RUNNING SMART TV APPLICATION UNIT TESTS        ");
        System.out.println("==================================================");

        testPowerLifecycle();
        testVolumeBoundaries();
        testChannelSwitching();
        testMuteFunctionality();
        testChannelRepositoryOperations();

        System.out.println("==================================================");
        System.out.println("TEST RESULTS: " + passed + " PASSED, " + failed + " FAILED.");
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertEquals(Object expected, Object actual, String testName) {
        if (expected == null && actual == null || (expected != null && expected.equals(actual))) {
            System.out.println(" [PASS] " + testName);
            passed++;
        } else {
            System.err.println(" [FAIL] " + testName + " - Expected: " + expected + ", Actual: " + actual);
            failed++;
        }
    }

    private static void assertTrue(boolean condition, String testName) {
        if (condition) {
            System.out.println(" [PASS] " + testName);
            passed++;
        } else {
            System.err.println(" [FAIL] " + testName + " - Expected true, got false.");
            failed++;
        }
    }

    private static void testPowerLifecycle() {
        Television tv = new Television("LG", 55);
        ChannelRepository repo = new InMemoryChannelRepository();
        TvService service = new TvServiceImpl(tv, repo);

        assertTrue(!service.isPoweredOn(), "TV initially should be OFF");
        service.powerOn();
        assertTrue(service.isPoweredOn(), "TV should be ON after powerOn()");
        service.powerOff();
        assertTrue(!service.isPoweredOn(), "TV should be OFF after powerOff()");
    }

    private static void testVolumeBoundaries() {
        Television tv = new Television("Sony", 65);
        ChannelRepository repo = new InMemoryChannelRepository();
        TvService service = new TvServiceImpl(tv, repo);

        // TV kapalıyken ses değiştirilemez
        try {
            service.volumeUp();
            assertTrue(false, "Should have thrown TvPoweredOffException when TV is off");
        } catch (TvPoweredOffException e) {
            assertTrue(true, "Properly throws TvPoweredOffException when altering volume while OFF");
        }

        service.powerOn();
        service.setVolume(Television.MAX_VOLUME);
        assertEquals(Television.MAX_VOLUME, tv.getVolume(), "Volume set to maximum");

        // Max volume overreach clamp
        service.volumeUp();
        assertEquals(Television.MAX_VOLUME, tv.getVolume(), "Volume should clamp at max boundary");

        service.setVolume(Television.MIN_VOLUME);
        assertEquals(Television.MIN_VOLUME, tv.getVolume(), "Volume set to minimum");

        // Min volume underreach clamp
        service.volumeDown();
        assertEquals(Television.MIN_VOLUME, tv.getVolume(), "Volume should clamp at min boundary");
    }

    private static void testChannelSwitching() {
        Television tv = new Television("Samsung", 75);
        ChannelRepository repo = new InMemoryChannelRepository();
        TvService service = new TvServiceImpl(tv, repo);
        service.powerOn();

        Channel ch2 = service.changeChannel(2);
        assertEquals(2, ch2.getNumber(), "Channel number should switch to 2");
        assertEquals("TRT Haber", ch2.getName(), "Channel name should match channel 2");

        Channel next = service.nextChannel();
        assertEquals(3, next.getNumber(), "Next channel from 2 should be 3");

        Channel prev = service.previousChannel();
        assertEquals(2, prev.getNumber(), "Previous channel from 3 should return to 2");
    }

    private static void testMuteFunctionality() {
        Television tv = new Television("Philips", 50);
        ChannelRepository repo = new InMemoryChannelRepository();
        TvService service = new TvServiceImpl(tv, repo);
        service.powerOn();

        service.setVolume(15);
        assertTrue(!tv.isMuted(), "TV initially should not be muted");

        service.toggleMute();
        assertTrue(tv.isMuted(), "TV should be muted after toggleMute()");

        // Ses artırıldığında sessiz mod kendiliğinden devre dışı kalmalıdır
        service.volumeUp();
        assertTrue(!tv.isMuted(), "Volume up should automatically unmute TV");
    }

    private static void testChannelRepositoryOperations() {
        ChannelRepository repo = new InMemoryChannelRepository();
        int initialCount = repo.count();

        Channel custom = new Channel(99, "Test Kanalı", ChannelCategory.GENEL, true);
        repo.add(custom);
        assertEquals(initialCount + 1, repo.count(), "Repository count should increment on add");

        assertTrue(repo.findByNumber(99).isPresent(), "Should find newly added channel");
        repo.removeByNumber(99);
        assertEquals(initialCount, repo.count(), "Repository count should decrement on remove");
    }
}
