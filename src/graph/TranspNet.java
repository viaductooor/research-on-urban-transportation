package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import exception.GraphException;
import exception.GraphIOException;

public class TranspNet extends Graph<String, HashMap<String, String>> {
    public void addAttr(String initNode, String endNode, String attrName, String attrValue) throws GraphException {
        if (this.containsEdge(initNode, endNode)) {
            HashMap<String, String> map = this.getEdge(initNode, endNode);
            if (map == null) {
                map = new HashMap<String, String>();
            }
            map.put(attrName, attrValue);
        } else {
            throw new GraphException("The edge " + initNode + "->" + endNode + "cannot be found in this graph.");
        }
    }

    public void addAttrAnyway(String initNode, String endNode, String attrName, String attrValue) {
        try {
            addAttr(initNode, endNode, attrName, attrValue);
        } catch (GraphException e) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(attrName, attrValue);
            this.addDiEdge(initNode, endNode, map);
        }
    }

    /**
     * Init the TranspNet with data from a csv file. Notice that the first two
     * columns of the file should be initNodes and endNodes.
     * 
     * @param url
     * @param attrs          array of attribute names
     * @param omitLineNumber
     * @throws GraphIOException
     */
    public void initFromCsv(String url, String[] attrs, int omitLineNumber) throws GraphIOException {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(new File(url)));
            int n = 0;
            while (n++ < omitLineNumber) {
                reader.readNext();
            }
            String[] strs = null;
            while ((strs = reader.readNext()) != null) {
                if (attrs.length != strs.length - 2) {
                    reader.close();
                    throw new GraphIOException("Number of attributes is not compatible. \n"
                            + "Check argument attrs and the corresponding parts in the csv file.");
                }
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < attrs.length; i++) {
                    map.put(attrs[i], strs[i + 2]);
                }
                this.addDiEdge(strs[0], strs[1], map);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add several attributes from a csv file. Each line of the csv file (except the
     * first line if you set omitLineNumber = 1) should be composed of (initNodeId,
     * endNodeId, attrToAdd_1,attrToAdd_2,...).
     * 
     * @param url
     * @param attrs
     * @param omitLineNumber
     * @throws GraphIOException
     * @throws GraphException
     */
    public void addAttrsFromCsv(String url, String[] attrs, int omitLineNumber)
            throws GraphIOException, GraphException {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(new File(url)));
            int n = 0;
            while (n++ < omitLineNumber) {
                reader.readNext();
            }
            String[] strs = null;
            while ((strs = reader.readNext()) != null) {
                if (attrs.length != strs.length - 2) {
                    reader.close();
                    throw new GraphIOException("Number of attributes is not compatible. \n"
                            + "Check argument attrs and the corresponding parts in the csv file.");
                }
                HashMap<String, String> map = null;
                if (!this.containsEdge(strs[0], strs[1])) {
                    map = new HashMap<String, String>();
                } else {
                    map = this.getEdge(strs[0], strs[1]);
                    if (map == null) {
                        map = new HashMap<String, String>();
                    }
                    for (int i = 0; i < attrs.length; i++) {
                        map.put(attrs[i], strs[i + 2]);
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a single attribute from a csv file. Each line of the csv file (except the
     * first line if you set omitLineNumber = 1) should be composed of (initNodeId,
     * endNodeId, attrToAdd).
     * 
     * @param url
     * @param attr
     * @param omitLineNumber
     * @throws GraphIOException
     */
    public void addAttrFromCsv(String url, String attr, int omitLineNumber) throws GraphIOException {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(new File(url)));
            int n = 0;
            while (n++ < omitLineNumber) {
                reader.readNext();
            }
            String[] strs = null;
            while ((strs = reader.readNext()) != null) {
                if (strs.length != 3) {
                    reader.close();
                    throw new GraphIOException("Every row of the csv file has to contain three items, "
                            + "greater or less than three is not allowed");
                }
                HashMap<String, String> map = null;
                if (!this.containsEdge(strs[0], strs[1])) {
                    map = new HashMap<String, String>();
                } else {
                    map = this.getEdge(strs[0], strs[1]);
                    if (map == null) {
                        map = new HashMap<String, String>();
                    }
                    map.put(attr, strs[2]);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use function func to check if the edge is valid, and add {"valid":"1"} to the
     * edge if it is valid, otherwise add {"valid":"0"}.
     * 
     * @param func
     */
    public void validityCheck(Function<HashMap<String, String>, Boolean> func) {
        for (Graph.Entry<String, HashMap<String, String>> e : this.entrySet()) {
            if (func.apply(e.getLink())) {
                e.getLink().put("valid", "1");
            } else {
                e.getLink().put("valid", "0");
            }
        }
    }

    @Override
    public void writeToCsv(String url) throws GraphIOException{
        File file = new File(url);
		File parent = new File(file.getParent());
		if(!parent.exists()){
			parent.mkdirs();
		}
        
        Function<List<String>,String[]> list2array = (List<String> list)->{
			String[] arr = new String[list.size()];
			list.toArray(arr);
			return arr;
		};
        
        CSVWriter  writer = null;
        List<String> headerList = new ArrayList<String>();
        headerList.add("begin");
        headerList.add("end");
        try {
            writer = new CSVWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(entrySet().size()<1){
            throw new GraphIOException("The graph is empty thus cannot be written into a csv file.");
        }
        
        //Write header to csv
        HashMap<String,String> firstEdge = entrySet().get(0).getLink();
        if(firstEdge.size()>0){
            for(HashMap.Entry<String,String> e:firstEdge.entrySet()){
                headerList.add(e.getKey());
            }
        }

        String[] headerArr = list2array.apply(headerList);
        writer.writeNext(headerArr);

        //Write content to csv
        for(Graph.Entry<String,HashMap<String,String>> e:entrySet()){
            List<String> contentList = new ArrayList<String>();
            contentList.add(e.getBegin());
            contentList.add(e.getEnd());
            HashMap<String,String> edge = e.getLink();
            if(headerArr.length>2){
                for(int i = 2;i<headerArr.length;i++){
                    contentList.add(edge.get(headerArr[i]));
                }
                String[] contentArr = list2array.apply(contentList);
                writer.writeNext(contentArr);
            }
        }

        try {
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}