public class Notifications implements Observer{
    @Override
    public void update() {
        System.out.println("Wystompił nowy błąd!");
    }
}
