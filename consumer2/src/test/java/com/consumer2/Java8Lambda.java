package com.consumer2;

import com.gmallinterface.bean.User;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class Java8Lambda {

	/** x-> x*s
	 *  (x,y)->x+y
	 *  (x,y) =>{x++,y++}
	 * 箭头左右为一括号省略
	 * 上下文参数类型可省略
	 */
	/**
	 * lambda四大核心函数式接口
	 * 消费型接口 Consumer<T> void accept(T t);
	 * 供给型接口 Supplier<T> T get();
	 * 断言型接口 Predicate<T> boolean test(T t);
	 * 函数型接口 Function<T,R> R apply(T t);
	 */

	/** 消费对象
	 * @Description //TODO 消费型接口 Consumer<T> void accept(T t);
	 * @Date 2019/8/9
	 * @Param 
	 * @return 
	 * @Author daim
	 **/
	@Test
	public void test1() {
		test1("daim",(str)-> System.out.println("this name is:"+str));
	}

	/**定义特殊功能
	 * @Description //TODO 函数型接口 Function<T,R> R apply(T t);
	 * @Date 2019/8/9
	 * @Param
	 * @return
	 * @Author daim
	 **/
	@Test
	public void test2() {
		String strs = "012345678";
		List<String> stringList = new ArrayList<>();
		List<String> stringList1 = test2(strs, (str) -> {
			for (int i = 0; i < strs.length(); i++) {
				stringList.add(str.replace(i + "", "a"));
			}
			return stringList;
		});
		stringList1.forEach(System.out::println);
	}

	/** 条件判断
	 * @Description //TODO 断言型接口 Predicate<T> boolean test(T t);
	 * @Date 2019/8/9
	 * @Param
	 * @return
	 * @Author daim
	 **/
	@Test
	public void test3() {
		System.out.println(test3("a",(str)-> "a".equals(str)));
	}

	/** 提供对象
	 * @Description //TODO 供给型接口 Supplier<T> T get();
	 * @Date 2019/8/9
	 * @Param
	 * @return
	 * @Author daim
	 **/
	@Test
	public void test4() {
		List<Integer> nums = test4(20,()-> {
			Random random = new Random();
			return random.nextInt(100);
		});
		nums.forEach(System.out::println);
	}

	@Test
	public void test5() {
		List<Integer> nums =Arrays.asList(12,-23,5,44,52,8,42,42,21,11,4,-1,2223);
		List<User> userList1 =Arrays.asList(
				new User("张三","HUBEI",38,"女",5155.55),
				new User("李四","HENAN",19,"男",666.66),
				new User("圈圈","GUANGDONG",18,"男",777.77),
				new User("张三","SICHAUN",11,"女",777.99),
				new User("颇尔","QINHAI",85,"男",444.44),
				new User("方便","HUNAN",65,"女",2222.12),
				new User("维稳","TIANJIN",42,"女",555.55),
				new User("谷歌","BEIJING",74,"男",3556.32),
				new User("飞三","GUANGXI",24,"女",9999.1),
				new User("司分","YUNNAN",36,"女",66666.3),
				new User("房东","XIZANG",31,"男",1111.12)
		);
		/** stream流处理 借助于lambda四大核心函数来实现流操作。
		 * stream() 集合转串行流
		 * filter() 过设置的条件过滤出元素【底层是实现Predicate 断言型接口】
		 * limit() 获取指定数量的流
		 * collect() 流对象转集合（合流）
		 *forEach() 来迭代流中的每个数据 【底层是实现Consumer 消费型接口】
		 * sorted() 对流进行排序
		 * summaryStatistics() 统计功能
		 * map() 用于映射每个元素到对应的结果 【底层是实现Function 函数形接口】
		 */
//		List<User> collect = userList1.stream().filter((user) -> user.getAge() > 30 && user.getAge() < 60).limit(4).collect(Collectors.toList());
//		collect.forEach(System.out::println);
		userList1.stream().filter((user)->user.getAge()>30&&user.getAge()<70).map((user)->user.getName()).limit(2).collect(Collectors.toList());;
		Random random = new Random();
		IntSummaryStatistics stats = nums.stream().mapToInt((x) -> x).summaryStatistics();
		System.out.println("列表中最大的数 : " + stats.getMax());
		System.out.println("列表中最小的数 : " + stats.getMin());
		System.out.println("所有数之和 : " + stats.getSum());
		System.out.println("平均数 : " + stats.getAverage());
//		nums.stream().sorted().forEach(System.out::println);
//		List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");
//// 获取空字符串的数量
//		long count = strings.parallelStream().filter(string -> string.isEmpty()).count();
//		List<String>strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");
//		List<String> filtered = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
//
//		System.out.println("筛选列表: " + filtered);
//		String mergedString = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.joining("-"));
//		System.out.println("合并字符串: " + mergedString);
	}

	void test1(String name, Consumer<String> consumer){
		consumer.accept(name);
	}

	List<String> test2(String str, Function<String,List<String>> fun){
		return  fun.apply(str);
	}

	boolean test3(String str, Predicate<String> pre){
		return pre.test(str);
	}

	List<Integer> test4(Integer num, Supplier<Integer> sup){
		List<Integer> nums = new ArrayList<>();
		for (int i = 0; i <num ; i++) {
			nums.add(sup.get());
		}
		return nums;
	}

	//策略设计模式
	@Test
	public void text6(){
		List<User> userList1 =Arrays.asList(
				new User("张三","HUBEI",38,"女",5155.55),
				new User("李四","HENAN",19,"男",666.66),
				new User("圈圈","GUANGDONG",18,"男",777.77),
				new User("张三","SICHAUN",11,"女",777.99),
				new User("颇尔","QINHAI",85,"男",444.44),
				new User("方便","HUNAN",65,"女",2222.12),
				new User("维稳","TIANJIN",42,"女",555.55),
				new User("谷歌","BEIJING",74,"男",3556.32),
				new User("飞三","GUANGXI",24,"女",9999.1),
				new User("司分","YUNNAN",36,"女",66666.3),
				new User("房东","XIZANG",31,"男",1111.12)
		);
		//策略设计模式(lambda函数式编程思想)
		//年龄大于30的女性
		textCelue(userList1,x->x.stream().filter(y->y.getAge()>30&&y.getSex().equals("女")).collect(Collectors.toList()));
		//女性工资高于5555
		textCelue(userList1,x->x.stream().filter(y->y.getSex().equals("女")&&y.getSalary()>5555).collect(Collectors.toList()));

	}

	public void textCelue(List<User> userList,Celue celue){
		celue.myCelue(userList).stream().forEach(System.out::println);
	}



}
