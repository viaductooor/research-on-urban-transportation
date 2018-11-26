

- UE assignment
- Compute Marginal cost:

 

​          ![img](file:///C:/Users/JOHNSM~1/AppData/Local/Temp/msohtmlclip1/01/clip_image002.png)

​                   Note:   d = link length 

​                            n= number of lanes

​                            x= link volume

- Calculate Link surcharge

​         (1/n) * linkMarginalCost + (1-(1/n)) * linkToll_last)

- Update Demand

​         Traverseall OD pair until all shortest path total cost >= original ODpair cost

​                   Foreach OD pair {

​                            Findshortest path (with update link travel time cost and link surcharge);

​                            Calculatethe shortest path total cost; 

​                            Ifshortest path total cost < original OD pair cost {

​                                     Load5% of original demand;

​                                     Update link traveltime cost:

![img](file:///C:/Users/JOHNSM~1/AppData/Local/Temp/msohtmlclip1/01/clip_image004.png)

​                                               Note:    d = link length 

​                                                        n= number of lanes

​                                                        x= link volume

​                                     }

​                            }

 