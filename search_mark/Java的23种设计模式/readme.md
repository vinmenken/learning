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

#### 9、享元模式：减少创建重复对象数，通常这些对象都是一些简单，只需要改变部份变量，所以我们可以创建一个这样的简单对象，之后通过修改变量来达到目得；如我们需要在屏幕上画小草数量（坐标定位xy、色）非常多，就能用到这种模式
- 1、注意：这种模式不能用在多线程中，不安全

#### 10、责任链模式:一个费用申请的审批流程，费用的多少是确定审批人是谁的唯一条件；
- 1、创建一个抽象类（审批人），抽象方法：设置下一个审批人方法、处理方法
- 2、实现抽象类（具体审批人）
```
public abstract class AbstractApprover {
    AbstractApprover successor;
    String name;
    AbstractApprover(String name){
        this.name = name;
    }

    /**
     * 处理请求
     * @param request 请求
     */
    public abstract void processRequest(PurchaseRequest request);
    public void setSuccessor(AbstractApprover successor){
        this.successor = successor;
    }
}
```
```
public class DepartmentApprover extends AbstractApprover {
    public DepartmentApprover(String name){
        super(name + " DepartmentLeader");
    }
    @Override
    public void processRequest(PurchaseRequest request) {
        if ((5000<= request.getSum()) && (request.getSum() < 10000)){
            System.out.println("**This request " + request.getID() + " will be handled by " + this.name + " **");
        }else {
            successor.processRequest(request);
        }
    }
}
```
```
public class GroupApprover extends AbstractApprover {
    public GroupApprover(String name){
        super(name + " GroupLeader");
    }
    @Override
    public void processRequest(PurchaseRequest request) {
        if (request.getSum() < 5000){
            System.out.println("**This request " + request.getID() + " will be handled by " + this.name + " **");
        }else {
            successor.processRequest(request);
        }
    }
}
```
```
public class MainTest {
    public static void main(String[] args) {
        Client client = new Client();
        AbstractApprover groupLeader = new GroupApprover("Tom");
        AbstractApprover departmentLeader = new DepartmentApprover("Jerry");
        AbstractApprover vicePresident = new VicePresidentApprover("Kate");
        AbstractApprover president = new PresidentApprover("Bush");
        groupLeader.setSuccessor(departmentLeader);
        departmentLeader.setSuccessor(vicePresident);
        vicePresident.setSuccessor(president);
        president.setSuccessor(groupLeader);
        groupLeader.processRequest(client.sendRequest(1, 100, 40));
        groupLeader.processRequest(client.sendRequest(2, 200, 40));
        groupLeader.processRequest(client.sendRequest(3, 300, 40));
        groupLeader.processRequest(client.sendRequest(4, 400, 140));
    }
}
```

#### 11、命令模式：数据驱动的设计模式、每一个命令都有一个类实现命令接口、如：我们家里有没个区域都有灯：客厅灯、房间灯，但每一个灯功能都不一样：如客厅的灯不单指能开关，还有调节亮度；房间的灯有暖色和冷色，最后我们可以通过一个控制器来操作；
- 1、创建命令接口：把这些灯的操作抽象成接口，只有开和关方法
- 2、实现命令接口：通过传入实际执行对象（灯的驱动）来实现接口命令方法（执行方法），每一个命令创建一个专用的类
- 3、通过一个控制类来控制所有灯的开关、控制亮度等
```
public interface Command {
    /**
     * 执行命令
     */
    public void execute();

    /**
     * 命令回退
     */
    public void undo();
}
```
```
//灯关的实现类
public class LightOffCommand implements Command {
    private Light light;
    public LightOffCommand(Light light){
        this.light = light;
    }
    public void execute() {
        light.off();
    }

    public void undo() {
        light.on();
    }
}
```
```
//灯开的实现类
public class LightOnCommand implements Command {
    private Light light;
    public LightOnCommand(Light light){
        this.light = light;
    }
    public void execute() {
        light.on();
    }

    public void undo() {
        light.off();
    }
}
```
```
public class ControlTest {
    public static void main(String[] args) {
        CommandModeControl control = new CommandModeControl();//控制类
        MarcoCommand onMarco, offMarco; 
        Light bedroomLight = new Light("BedRoom"); //灯的驱动
        Light kitchenLight = new Light("Kitchen"); //灯的驱动
        Stereo stereo = new Stereo(); //音响的驱动

        LightOnCommand bedroomLightOn = new LightOnCommand(bedroomLight); //灯的每一个命令都是一个对象
        LightOffCommand bedroomLightOff = new LightOffCommand(bedroomLight); //灯的每一个命令都是一个对象
        LightOnCommand kitchenLightOn = new LightOnCommand(kitchenLight); //灯的每一个命令都是一个对象
        LightOffCommand kitchenLightOff = new LightOffCommand(kitchenLight); //灯的每一个命令都是一个对象

        Command[] onCommands = {bedroomLightOn, kitchenLightOn}; //所有灯的开
        Command[] offCommands = {bedroomLightOff, kitchenLightOff}; //所有灯的关
        onMarco = new MarcoCommand(onCommands); 
        offMarco = new MarcoCommand(offCommands);

        StereoOnCommand stereo1On = new StereoOnCommand(stereo); //音响的每一个命令都是一个对象
        StereoOffCommand stereo1Off = new StereoOffCommand(stereo); //音响的每一个命令都是一个对象
        StereoAddVolCommand stereoAddVol = new StereoAddVolCommand(stereo); //音响的每一个命令都是一个对象
        StereoSubVolCommand stereoSubVol = new StereoSubVolCommand(stereo); //音响的每一个命令都是一个对象

        control.setCommand(0, bedroomLightOn, bedroomLightOff); //每个按钮都有不一样的命令
        control.setCommand(1, kitchenLightOn, kitchenLightOff);
        control.setCommand(2, stereo1On, stereo1Off);
        control.setCommand(3, stereoAddVol, stereoSubVol);
        control.setCommand(4, onMarco, offMarco);

        control.onButton(0);
        control.undoButton();
        control.onButton(1);
        control.offButton(1);
        control.onButton(2);
        control.onButton(3);
        control.offButton(3);
        control.undoButton();
        control.offButton(2);
        control.undoButton();
        control.onButton(4);
        control.offButton(4);
    }
}
```

#### 12、解释器模式：用的很少

#### 13、迭代器模式：
- 1、访问一个聚合对象的内容而无须暴露它的内部表示。 
- 2、需要为聚合对象提供多种遍历方式。 
- 3、为遍历不同的聚合结构提供一个统一的接口。
```
public interface Iterator {
   public boolean hasNext();
   public Object next();
}
```
```
public interface Container {
   public Iterator getIterator();
}
```
```
public class NameRepository implements Container {
   public String names[] = {"Robert" , "John" ,"Julie" , "Lora"};
 
   @Override
   public Iterator getIterator() {
      return new NameIterator();
   }
 
   private class NameIterator implements Iterator { //内部类
 
      int index;
 
      @Override
      public boolean hasNext() {
         if(index < names.length){
            return true;
         }
         return false;
      }
 
      @Override
      public Object next() {
         if(this.hasNext()){
            return names[index++];
         }
         return null;
      }     
   }
}
```
```
public class IteratorPatternDemo {
   
   public static void main(String[] args) {
      NameRepository namesRepository = new NameRepository();
 
      for(Iterator iter = namesRepository.getIterator(); iter.hasNext();){
         String name = (String)iter.next();
         System.out.println("Name : " + name);
      }  
   }
}
```

#### 14、中介者模式：有几个不同的对象A B C D 当某一个对象A发生了操作后，其余几个对象都会有相应不同的操作，如果这个时候每个类都写相应操作的业务逻辑，关联性非常常高，解决方法，通过一个中间类，把所有对象都注册到该对象中（把中间对象给到 其它对象中的变量），当某一对象发生操作后直接通知其它对象；
```
public class MainTest {
    public static void main(String[] args) {
        Mediator mediator = new ConcreteMediator(); //中间类
        Alarm alarm = new Alarm(mediator, "alarm"); //在创建对象时把 中间对象 赋值给这个alarm 并 调用中间对象把这个alarm对象注册到中间对象中
        CoffeeMachine coffeeMachine = new CoffeeMachine(mediator, "coffeeMachine"); //同上
        Curtains curtains = new Curtains(mediator, "curtains"); //同上
        TV tv = new TV(mediator, "tv"); //同上
        alarm.sendMessage(0);
        coffeeMachine.finishCoffee();
        alarm.sendMessage(1);
    }
}
/////////////////////////
public interface Mediator {
    /**
     * 注册
     * @param colleagueName 名字
     * @param colleague 类型
     */
    public abstract void register(String colleagueName, AbstractColleague colleague);

    /**
     * 得到消息
     * @param stateChange 状态
     * @param colleagueName 名字
     */
    public abstract void getMessage(int stateChange, String colleagueName);
    public abstract void sendMessage();
}
/////////////////////////
public abstract class AbstractColleague {
    private Mediator mediator;
    public String name;
    AbstractColleague(Mediator mediator, String name){
        this.mediator = mediator;
        this.name = name;
    }
    Mediator getMediator(){
        return this.mediator;
    }

    /**
     * 发送消息
     * @param stateChange 状态
     */
    public abstract void sendMessage(int stateChange);
}
/////////////////////////
public class Alarm extends AbstractColleague {
    public Alarm(Mediator mediator, String name){
        super(mediator, name);
        mediator.register(name, this);
    }
    public void sendAlarm(int stateChange){
        sendMessage(stateChange);
    }
    @Override
    public void sendMessage(int stateChange) {
        this.getMediator().getMessage(stateChange, this.name);
    }
}
```

#### 15、备忘录模式：保存对象状态
- 1、创建一个接口类MementoIF，为空，不需要有方法
- 2、在对象中 添加内部类Memento实现上面的接口（定义为私有）
- 3、创建MementoCaretaker类，用于状态的保存与还原（只是记录状态保存的路径，具体保存到哪里由对象本身实现（内部类Memento））
```
public class MainTest {
    public static void main(String[] args) {
        MementoCaretaker mementoCaretaker = new MementoCaretaker(); //管理对象状态类
        Originator originator = new Originator(); 
        Originator2 originator2 = new Originator2();
        System.out.println("***Originator***");
        originator.testState1();
        mementoCaretaker.saveMemento("Originator", originator.createMemento());
        originator.showState();
        originator.testState2();
        originator.showState();
        originator.restoreMemento(mementoCaretaker.retrieveMemento("Originator"));
        originator.showState();

        System.out.println("***Originator2***");
        originator2.testState1();
        originator2.showState();
        mementoCaretaker.saveMemento("Originator2", originator2.createMemento());
        originator2.testState2();
        originator2.showState();
        originator2.restoreMemento(mementoCaretaker.retrieveMemento("Originator2"));
        originator2.showState();

        System.out.println("***Originator&&Originator2***");
//        originator.restoreMemento(mementoCaretaker.retrieveMemento("Originator2"));
//        originator.showState();
    }
}
///////////////////////
public class MementoCaretaker {
    private HashMap<String, MementoIF> mementoMap;
    public MementoCaretaker(){
        mementoMap = new HashMap<String, MementoIF>();
    }
    public MementoIF retrieveMemento(String name){ //还原状态
        return mementoMap.get(name);
    }
    public void saveMemento(String name, MementoIF memento){ //保存状态对象方法
        this.mementoMap.put(name, memento);
    }
}
///////////////////////
interface MementoIF { //接口类 为空
}
///////////////////////
public class Originator {
    private HashMap<String, String> state;
    public Originator(){
        state = new HashMap<String, String>();
    }
    public MementoIF createMemento(){
        return new Memento(state);
    }
    public void restoreMemento(MementoIF memento){
        state = ((Memento) memento).getState();
    }
    public void showState(){
        System.out.println("now state:" + state.toString());
    }
    public void testState1(){
        state.put("blood", "500");
        state.put("progress", "gate1 end");
        state.put("enemy", "5");
    }
    public void testState2(){
        state.put("blood", "450");
        state.put("progress", "gate3 start");
        state.put("enemy", "3");
    }
    private class Memento implements MementoIF{ //内部类
        private HashMap<String, String> state;
        private Memento(HashMap state){
            this.state = new HashMap<String, String>(state);
        }
        private HashMap getState(){
            return state;
        }
        private void setState(HashMap state){
            this.state = state;
        }
    }
}

```

