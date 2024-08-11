public class MissingImports {
    private Random random = new Random();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private File file = new File("example.txt");
    private LocalDate localDate = LocalDate.now();
    private ZoneId zoneId = ZoneId.systemDefault();

    public void printRandomNumber() {
        System.out.println(random.nextInt(100));
    }

    public void printCurrentDate() {
        System.out.println(dateFormat.format(new Date()));
    }

    public void printFileExists() {
        System.out.println(file.exists());
    }

    public void printLocalDate() {
        System.out.println(localDate);
    }

    public void printZoneId() {
        System.out.println(zoneId);
    }
}
