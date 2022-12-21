# xml-processor

1. install java 11.
2. add file name & pr customer ref mapping under "config/prcustref-mapping.txt" as below format.
   Ex: GSBR01AC-101447915_1_221215.xml,101388783
3. put all the bill xml files under "input" folder.
   ex: GSBR01AC-101447915_1_221215.xml
4. run below command in the root directory.
   java  -jar rbm-xml-process-0.0.1-SNAPSHOT.jar
5. you can see modified xmls under "output" folder.
6. check log for further info.