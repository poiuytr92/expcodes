package exp.libs.warp.db.nosql;

import java.util.List;

import redis.clients.jedis.Jedis;

public class RedisUtils {

	public static void main(String[] args) {
		testConnection();
	}

	public static void testConnection() {
		// Connecting to Redis server on localhost
		Jedis jedis = new Jedis("192.168.182.128");
		System.out.println("Connection to server sucessfully");
		// check whether server is running or not
		System.out.println("Server is running: " + jedis.ping());
		System.out.println("Stored string in redis: " + jedis.get("wife"));

		List<String> list = jedis.lrange("time-unit", 0, 5);
		for (int i = 0; i < list.size(); i++) {
			System.out.println("Stored string in redis:: " + list.get(i));
		}
	}

}
