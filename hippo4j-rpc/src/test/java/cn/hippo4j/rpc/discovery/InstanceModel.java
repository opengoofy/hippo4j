package cn.hippo4j.rpc.discovery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.test.context.TestComponent;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TestComponent
public class InstanceModel {

    String name;
}
