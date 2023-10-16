package io.github.hello09x.fakeplayer.manager.naming;

import java.util.LinkedList;

public class NameSource {

    /**
     * 接下来可以使用的名称序号
     */
    private final LinkedList<Integer> names;

    /**
     * 容量
     */
    private volatile int capacity;

    public NameSource(int initializeCapacity) {
        this.capacity = initializeCapacity;
        this.names = new LinkedList<>();
        for (int i = 0; i < initializeCapacity; i++) {
            names.add(i);
        }
    }

    public NameSource() {
        this(0);
    }

    /**
     * 获取一个可使用的名称序号
     *
     * @return 名称序号
     */
    public synchronized int pop() {
        if (names.isEmpty()) {
            var newCapacity = capacity * 2;
            for (int i = capacity; i < newCapacity; i++) {
                names.add(i);
            }
            this.capacity = newCapacity;
        }
        return names.pop();
    }

    /**
     * 归还一个名称序号
     *
     * @param i 名称序号
     */
    public synchronized void push(int i) {
        if (i >= capacity) {
            return;
        }

        if (names.contains(i)) {
            return;
        }

        names.push(i);
    }


}
