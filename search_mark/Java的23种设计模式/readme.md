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

#### 3、原型模型：简单说就是把对象复制
- 1、如果必须要频繁new对象，可以考虑原型模型，提高性能
- 2、一般和工厂模式一起使用，原因与1一样

#### 4、适配器模型：将传入的对象，实现接口方法
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
