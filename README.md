# RileyCache
This project is for CAT interview<br><br>
Assume the default get() and put() are to operate directly to database or file system. We call them "real target"<br>
This implementation is to make a 2 level cache before hiting the real target. I mock the real targe with a class called "ServiceImplReal" in the package, "Service"<br><br>
Level 1 is a linklist in the memory, and level 2 is a folder in hard disk that stores the converted objects. When the system push an object to level 2, the object will be converted to a file, and the key will be the file name.<br>
Level 1 has its capacity. The level is full, the oldest node, which is the tail of the linklist will be detached, and it will be saved to level 2.


## Overridden Methods:
### get(key)
1. If the key exist in cache level 1, the system will move that node to the front of the linklist and return the value.<br>
2. If the key exist in cache level 2, the system will add a node, with the corresponding key and the object as value to the front of the linklist. After that, the object will be returned. The file with the key in level 2 will be deleted. Further, if level 1 exceeds its capacity, it will detach the last node and save that to level 2.<br>
3. If they key does not exist in both level 1 and level 2, it will call default method to get data from "real target." In the mock object, "ServiceImplReal," it will just return ["value of " + key].

### put(key,value)
1. If the key exist in cache level 1, the system will move that node to the front of the linklist and update the value, which is the updated object.<br>
2. If the key exist in cache level 2, the system will add a node, with the corresponding key and the updated object as value to the front of the linklist. The file with the key in level 2 will be deleted. Further, if level 1 exceeds its capacity, it will detach the last node and save that to level 2.<br>


## JUnit Test:
There are 3 test cases in JUnit test. The details are written in the class.
