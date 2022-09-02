import java.nio.charset.StandardCharsets;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.kv.GetResponse;

/**
 *@author : wh
 *@date : 2022/9/2 20:08
 *@description:
 */
public class Test {


	public static void main(String[] args) throws Exception{

		Client client = Client.builder().endpoints("http://127.0.0.1:2379").build();
		GetResponse getResponse = client.getKVClient().get(ByteSequence.from("/thread", StandardCharsets.UTF_8)).get();
		System.out.println("hahah");
	}
}
