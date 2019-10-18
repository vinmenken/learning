#### 1、策略模式：可以实现目标的方案集合 为 一个策略
- 1、创建一个接口，抽象出方法；
- 2、实现该接口
- 3、通过上下文类（context）来使用某一种策略

具体 [代码](https://www.runoob.com/design-pattern/strategy-pattern.html)
```
public class Context {
   private Strategy strategy; //策略接口
 
   public Context(Strategy strategy){
      this.strategy = strategy;
   }
 
   public int executeStrategy(int num1, int num2){
      return strategy.doOperation(num1, num2); //每一个策略都会实现的方法
   }
}
```
```
public class StrategyPatternDemo {
   public static void main(String[] args) {
      Context context = new Context(new OperationAdd()); //创建上下文对象关指定使用哪一种策略    OperationAdd、OperationSubstract、OperationMultiply 不同的策略实现类
      System.out.println("10 + 5 = " + context.executeStrategy(10, 5));
 
      context = new Context(new OperationSubstract());      
      System.out.println("10 - 5 = " + context.executeStrategy(10, 5));
 
      context = new Context(new OperationMultiply());    
      System.out.println("10 * 5 = " + context.executeStrategy(10, 5));
   }
}
```

#### 2、观察者模式：对象间存在一对多关系；一个对象有信息需要通知其它对象
##### java有内置的观察者类 只需要 继承 Observable类 就会有 注册、移除、通知方法
- 1、将需要通知的所有对象，抽象成一个接口（Observer），接口中的方法就是被通知对象需要知道的方法
- 2、实现该接口
- 3、创建Subject类，添加 实现类对象(Observer)
- 4、当subject对象有消息要知道Observer对象时，通过for 来调用 Observer的方法
```
public interface Subject { // Observable类
    public void registerObserver(Observer observer); //注册
    public void removeObserver(Observer observer); //移除
    public void notifyObservers(); //通知
}
```

#### 3、原型模式：简单说就是把对象复制
- 1、如果必须要频繁new对象，可以考虑原型模型，提高性能
- 2、一般和工厂模式一起使用，原因与1一样

#### 4、适配器模式：将传入的对象，实现接口方法
- 1、确定需要适配的接口
- 2、通过构造函数传入 需要适配的对象
- 3、实现该接口（依赖传入的对象）
```
public class EnumerationIterator implements Iterator<Object> {
    private Enumeration enumeration;
    public EnumerationIterator(Enumeration enumeration){
        this.enumeration = enumeration;
    }
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    public Object next() {
        return enumeration.nextElement();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
```
```
public class TurkeyAdapter implements Duck {
    private Turkey turkey;
    public TurkeyAdapter(Turkey turkey){
        this.turkey = turkey;
    }
    public void quack() {
        turkey.gobble();
    }

    public void fly() {
        for (int i=0; i<6; i++){
            turkey.fly();
        }
    }
}
```
```
public class TurkeyAdapter2 extends WildTurkey implements Duck {
    public void quack() {
        super.gobble();
    }

    @Override
    public void fly() {
        super.fly();
        super.fly();
        super.fly();
    }
}
```

#### 5、桥接模式：一个房子里面有客厅、房间、洗手间，创建房子对象需要给房子的客厅、房间、洗手间变量赋值（耦合度高），当房间需要装修，只能直接修改原来的房间类或者继承原来的房间类，如果将房间抽象出一个接口，就简单多了只需要实现这个接口就行，同样房子也可以抽象出抽象类（换房子也方便了）；一个接口，一个抽象类；
```
public abstract class AbstractHome {
    Toilet toilet = null;
    AbstractTvControls(Toilet toilet) {
        this.toilet = toilet;
    }
    public abstract void 方法名();
}
```
```
public interface Toilet {}
```

#### 6、过滤模式
- 1、创建一个过滤接口（Criteria）
- 2、实现接口
- 3、创建实现类对象，把要过滤的对象赋值给该对象
```
public interface Criteria {
   public List<Person> meetCriteria(List<Person> persons); //persons就是被过滤对象
}
```

#### 7、组合模式：员工对象有一个集合变量，用来保存员工；ceo直接下属：销售经理和开发经理；销售经理直接下属：销售1、销售2；开发经理直接下属：开发员1、开发员2；
```
public class Employee {
   private String name;
   private String dept;
   private int salary;
   private List<Employee> subordinates;
```
```
public static void main(String[] args) {
      Employee CEO = new Employee("John","CEO", 30000);
 
      Employee headSales = new Employee("Robert","Head Sales", 20000);
 
      Employee headMarketing = new Employee("Michel","Head Marketing", 20000);
 
      Employee clerk1 = new Employee("Laura","Marketing", 10000);
      Employee clerk2 = new Employee("Bob","Marketing", 10000);
 
      Employee salesExecutive1 = new Employee("Richard","Sales", 10000);
      Employee salesExecutive2 = new Employee("Rob","Sales", 10000);
 
      CEO.add(headSales);
      CEO.add(headMarketing);
```

#### 8、外观模式：隐藏系统的复杂性，提供简单的方法；如：回到家我想听音乐，但我需要开音箱、cd机、功放，这些操作太过多了，我现在想只需要按一个按钮就能把上面所有的设备启动；这就是外观模式了
```
public class MainTest {
    public static void main(String[] args) {
        HomeTheaterFacade homeTheaterFacade = new HomeTheaterFacade();
        homeTheaterFacade.ready();
        homeTheaterFacade.play();
    }
}
```
```
public class HomeTheaterFacade {
    private TheaterLights theaterLights;
    private Popcorn popcorn;
    private Stereo stereo;
    private Projector projector;
    private Screen screen;
    private DvdPlayer dvdPlayer;
    public HomeTheaterFacade(){
        theaterLights = TheaterLights.getInstance();
        popcorn = Popcorn.getInstance();
        stereo = Stereo.getInstance();
        projector = Projector.getInstance();
        screen = Screen.getInstance();
        dvdPlayer = DvdPlayer.getInstance();
    }
    public void ready(){
        popcorn.on();
        popcorn.pop();
        screen.down();
        projector.on();
        stereo.on();
        dvdPlayer.on();
        dvdPlayer.setDvd();
        theaterLights.dim(10);
    }
    public void end(){
        popcorn.off();
        theaterLights.bright();
        screen.up();
        projector.off();
        stereo.off();
        dvdPlayer.setDvd();
        dvdPlayer.off();
    }
```

      Employee clerk2 = new Employee("Bob","Marketing", 10000);供
      Employee clerk2 = new Employee("Bob","Marketing", 10000);
