package org.apache.maven.project.impl;

import org.apache.maven.project.ModelProperty;
import org.apache.maven.project.ModelUri;
import org.apache.maven.project.ModelProcessor;

import java.util.*;

/**
 *
 */
public class DependencyManagementProcessor implements ModelProcessor {

    private static boolean matchUriValuesFromMaps(Map<String, ModelProperty> x, Map<String, ModelProperty> y, String uri) {
        ModelProperty xUri = x.get(uri);
        ModelProperty yUri = y.get(uri);
        return !(xUri == null || yUri == null) && xUri.getValue().equals(yUri.getValue());
    }

    private static void printMap(Map<String, ModelProperty> map) {
        for (ModelProperty mp : map.values()) {
            System.out.println(mp);
        }
    }

    protected void pushDependenciesFromQueue(LinkedList<ModelProperty> workQueue, Stack<ModelProperty> resultStack) {
        Map<String, ModelProperty> x = pushDependencyFromQueue(workQueue, resultStack);

        LinkedList<ModelProperty> tmpWorkQueue = new LinkedList<ModelProperty>();
        //tmpWorkQueue.addAll(workQueue);
        int deadStop = 0;
        while (true) {
            if(workQueue.size() <=0 ) {
                break;
            }
            System.out.println("Iteration: " + deadStop++ +":" + workQueue.size());
            Map<String, ModelProperty> y = inspectDependencyFromQueue(workQueue);

            int rowCount = y.size();

            System.out.println("*------x------");
            printMap(x);
            System.out.println("+-----y-------");
            printMap(y);
            System.out.println("=-----------");

            if (matchUriValuesFromMaps(x, y, ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri()) &&
                    matchUriValuesFromMaps(x, y, ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri())) {

                System.out.println("Matched GROUPID and ARTIFACT ID");

                if (matchUriValuesFromMaps(x, y, ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri())) {
                    System.out.println("MATCHED VERSION");
                    Set<String> yk = y.keySet();
                    yk.removeAll(x.keySet());
                    for (String key : yk) {
                        System.out.println("KEY = " + key);
                        resultStack.push(workQueue.get(workQueue.indexOf(y.get(key))));
                    }
                }
                for (int i = 0; i < rowCount; i++) {
                    System.out.println("Remove dependency from work queue" + workQueue.poll());
                }
            } else {//noop
                for (int i = 0; i < rowCount; i++) {
                    ModelProperty mp = workQueue.poll();
                    System.out.println("WQ -> TQ: " + mp);
                      tmpWorkQueue.push(mp);
                }
            }
        }
        System.out.println("TQ -> WQ");
        for (ModelProperty p : tmpWorkQueue) {
            System.out.println(p);
        }
        workQueue.clear();
        workQueue.addAll(tmpWorkQueue);
        // workQueue.addAll(tmpWorkQueue);
    }

    public void process(List<ModelProperty> list) {

        int[] i = getIndexForDependencies(list);
        if (i[0] == -1) {
            return;
        }
        List<ModelProperty> managementDependencyList = list.subList(i[0], i[1]);
        System.out.println("Extracted List");
        for (ModelProperty mp : managementDependencyList) {
            // System.out.println(mp);
        }
        LinkedList<ModelProperty> workQueue = new LinkedList<ModelProperty>(managementDependencyList);
        Collections.reverse(workQueue);

        Stack<ModelProperty> resultStack = new Stack<ModelProperty>();
        int j = 0;
        while (workQueue.size() > 0) {
            pushDependenciesFromQueue(workQueue, resultStack);
            System.out.println("Processed Stack step (" + j++ + ") Size = " + workQueue.size() + "\r\n");
        }
        managementDependencyList.clear();
        Collections.reverse(resultStack);
        list.addAll(i[0], resultStack);
    }

    private Map<String, ModelProperty> inspectDependencyFromQueue(LinkedList<ModelProperty> workQueue) {
        Map<String, ModelProperty> tmpMap = new HashMap<String, ModelProperty>();
        for (ModelProperty mp : workQueue) {
            tmpMap.put(mp.getUri(), mp);
            if (ModelUri.matches(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY, mp.getUri())) {
                break;
            }
        }
        return tmpMap;
    }

    /**
     * Pushes the URIs of the first dependency from the specified work queue to the specified result stack.
     *
     * @param workQueue
     * @param resultStack the result stack
     * @return map of properties of the pushed dependency, where the key is the URI and value is ModelProperty.
     */
    private Map<String, ModelProperty> pushDependencyFromQueue(LinkedList<ModelProperty> workQueue, Stack<ModelProperty> resultStack) {
        Map<String, ModelProperty> tmpMap = new HashMap<String, ModelProperty>();
        for (Iterator<ModelProperty> i = workQueue.iterator(); i.hasNext();) {
            ModelProperty mp = i.next();
            i.remove();
            System.out.println("WQ-> Result " + mp);
            resultStack.push(mp);
            tmpMap.put(mp.getUri(), mp);
            if (ModelUri.matches(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY, mp.getUri())) {
                break;
            }
        }
        return tmpMap;
    }

    private static int[] getIndexForDependencies(List<ModelProperty> modelList) {
        int startIndex = -1, endIndex = -1;
        for (int i = 0; i < modelList.size(); i++) {
            ModelProperty mp = modelList.get(i);
            if (ModelUri.matches(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY, mp.getUri()) && startIndex < 0) {
                startIndex = i;
            } else
            if (startIndex > -1 && !mp.getUri().startsWith(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri())) {
                endIndex = i;
                break;
            }
        }
        if (endIndex == -1) {
            endIndex = modelList.size();
        }
        return new int[]{startIndex, endIndex};
        // dependencyManagementList = modelList.subList(startIndex, endIndex);
        // return startIndex;
    }
}
