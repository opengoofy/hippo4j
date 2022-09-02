/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
