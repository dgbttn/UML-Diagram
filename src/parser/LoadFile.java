package parser;

import com.model.*;
import com.model.Class;
import com.util.StringUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadFile {

    public void Analyze(Project p, String path, String output){
        if(path.endsWith(".zip"))
            ReadFile(p, ZipExtracter.extract(path,output));
        else
            ReadFile(p,path);
    }

    public void writeAllRelarionships(Project p, String output){
        File file = new File("Relationship.txt");

        try {
            FileWriter fw = new FileWriter(output+ File.separator+file.getName(), false);
            BufferedWriter bw = new BufferedWriter(fw);
            for (ContainerStructure s : p.getStructures()){
                String data = "";
                if (s instanceof Class){
                    Class c = (Class) s;
                    data+= "Class "+c.getName();
                    if (c.getSuperClass()!=null) data+= " extends "+c.getSuperClass().getName();
                    if (c.getInterfaces().size()>0) {
                        data += " implements ";
                        for (Interface i : c.getInterfaces()) data+= i.getName()+", ";
                        data= data.substring(0,data.lastIndexOf(","));
                    }
                    data+= "\n";
                    bw.write(data);
                    continue;
                }
                Interface i = (Interface) s;
                data+= "Interface "+i.getName();
                if (i.getSuperInterfaces().size()>0) data += " implements ";
                for (Interface in : i.getSuperInterfaces()) data+= in.getName()+", ";
                data= data.substring(0,data.lastIndexOf(","));
                data+= "\n";
                bw.write(data);
            }

            bw.close();
        } catch (IOException e) {

        }

    }

    public static String readContentFromFile(String path){
        String data="";
        try {
            File file = new File(path);
            FileReader fileReader = new FileReader(file.getAbsolutePath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while (bufferedReader.ready())
                data += bufferedReader.readLine() + "\n";

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public void updateRelationship(Project p, String source) throws Exception{
        String nameS[] = Parser.getClassDeclaration(source);
        String name[] = nameS[0].split(" ");
        if (name[name.length-2].equals("class")) {
            Class c = p.getClassByName(name[name.length-1].trim());
            Class.Builder cb = p.getClassBuilder();
            cb = cb.withName(c.getName())
                    .withMethods(c.getMethods())
                    .withProperties(c.getProperties())
                    .isAbstract(c.isAbstract())
                    .implement(c.getInterfaces())
                    .extend(c.getSuperClass());
            if (nameS[1]!=null) cb= cb.extend(p.getClassByName(nameS[1].trim()));
            if (nameS[2]!=null) {
                for (String i : nameS[2].split(" ")) cb = cb.implement(p.getInterfaceByName(i.trim()));
            }
            try {
                Class cc = cb.build();
                if (cc!=null) p.replace(c,cc);
            } catch (StructureException e) {
            }
            return;
        }
        Interface i= p.getInterfaceByName(name[name.length-1].trim());
        Interface.Builder ib = p.getInterfaceBuilder();
        ib = ib.withName(i.getName())
                .withMethods(i.getMethods())
                .withProperties(i.getProperties())
                .extend(i.getSubInterfaces());
        if (nameS[2]!=null) {
            for (String in : nameS[2].split(" ")) ib = ib.extend(p.getInterfaceByName(in.trim()));
        }
        try {
            Interface ii = ib.build();
            p.replace(i,ii);
        } catch (StructureException e) {
        }
    }

    public void ReadFile(Project p, String path){
        File folder = new File(path);
        List<String> sourceCodes = new LinkedList<>();
        if (folder.getName().endsWith(".java")){
            String source = readContentFromFile(folder.getAbsolutePath());
            source = Parser.normalize(source);
            sourceCodes.add(source);
            try {
                createStructure(p,source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            File[] files = folder.listFiles();

            for (File file : files){
                if (file.isDirectory()) ReadFile(p, file.getAbsolutePath());
                if (file.isFile() && file.getName().endsWith(".java")) {
                    String source = readContentFromFile(file.getAbsolutePath());
                    source = Parser.normalize(source);
                    sourceCodes.add(source);
                    try {
                        createStructure(p,source);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //System.out.println(source);
                }
            }
        }

        for (String source : sourceCodes){
            try {
                updateRelationship(p,source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Method createMethod(Project p, String at){
        at= at.trim();
        Method.Builder mb = p.getMethodBuilder();
        String[] me = at.split("\\(");
        me[0]=me[0].trim();
        me[1]=me[1].trim();
        String[] meName = me[0].trim().split(" ");
        mb = mb.withName(meName[meName.length-1].trim());

        String type;
        switch (meName[0].trim()){
            case "public" :
                mb = mb.withVisibility(Structure.Visibility.PUBLIC);
                type = me[0].substring(6,me[0].indexOf(meName[meName.length-1])).trim();
                break;

            case "private" :
                mb = mb.withVisibility(Structure.Visibility.PRIVATE);
                type = me[0].substring(7,me[0].indexOf(meName[meName.length-1])).trim();
                break;

            case "protected" :
                mb = mb.withVisibility(Structure.Visibility.PROTECTED);
                type = me[0].substring(9,me[0].indexOf(meName[meName.length-1])).trim();
                break;

            case "default" :
                mb = mb.withVisibility(Structure.Visibility.DEFAULT);
                type = me[0].substring(7,me[0].indexOf(meName[meName.length-1])).trim();
                break;

            default:
                mb = mb.withVisibility(Structure.Visibility.DEFAULT);
                type = me[0].substring(0,me[0].indexOf(meName[meName.length-1])).trim();
                break;
        }

        if (type.length()<3) return null;
        if (me[0].indexOf("static")>-1) {
            mb = mb.isStatic(true);
            type = type.substring(6,type.length()).trim();
        }
        if (me[0].indexOf("abstract")>-1) {
            mb = mb.isAbstract(true);
            type = type.substring(8, type.length()).trim();
        }
        mb = mb.withReturnType(type);

        if (me[1].substring(0,me[1].indexOf(")")).trim().length()>0 && me[1].indexOf("<") < 0) {
            String[] arguments = me[1].substring(0,me[1].indexOf(")")).split(",");
            for (String arg : arguments) {
                mb.withArgument(new Argument(arg.split(" ")[1] , arg.split(" ")[0]));
            }
        }

        try {
            return mb.build();
        } catch (StructureException e) {
            System.out.println(1);
            return null;
        }
    }

    public Property createProperty(Project p, String at){
        Property.Builder pb = p.getPropertyBuilder();
        String[] meName = at.split(" ");
        pb = pb.withName(meName[meName.length-1]);
        if (at.indexOf("final")>-1 || at.indexOf("static")>-1) pb = pb.isStatic(true);
        switch (meName[0]){
            case "public" :
                pb = pb.withVisibility(Structure.Visibility.PUBLIC);
                pb = pb.withType(at.substring(6,at.indexOf(meName[meName.length-1])).replace(" ",""));
                break;

            case "private" :
                pb = pb.withVisibility(Structure.Visibility.PRIVATE);
                pb = pb.withType(at.substring(7,at.indexOf(meName[meName.length-1])).replace(" ",""));
                break;

            case "protected" :
                pb = pb.withVisibility(Structure.Visibility.PROTECTED);
                pb = pb.withType(at.substring(9,at.indexOf(meName[meName.length-1])).replace(" ",""));
                break;

            case "default" :
                pb = pb.withVisibility(Structure.Visibility.DEFAULT);
                pb = pb.withType(at.substring(7,at.indexOf(meName[meName.length-1])).replace(" ",""));
                break;

            default:
                pb = pb.withVisibility(Structure.Visibility.DEFAULT);
                pb = pb.withType(at.substring(0,at.indexOf(meName[meName.length-1])).replace(" ",""));
                break;
        }

        try {
            return pb.build();
        } catch (StructureException e) {
            return null;
        }
    }

    public void createStructure(Project p, String source) throws Exception{
        String nameS[] = Parser.getClassDeclaration(source);
        String name[] = nameS[0].split(" ");
        String[] attribute;
        if (name[name.length-2].equals("class")) {
            Class.Builder cb = p.getClassBuilder();
            cb = cb.withName(name[name.length-1].trim());
            if (nameS[0].indexOf("abstract")>-1) cb=cb.isAbstract(true);
            else cb =cb.isAbstract(false);
            attribute = source.split(";");
            for (int i=1; i<attribute.length; i++)
            {
                String at = attribute[i];
                if (at.indexOf("(")>-1) {
                    if (createMethod(p,at)!=null) cb.withMethod(createMethod(p,at));
                }
                else
                    if (createProperty(p,at)!=null) cb.withProperty(createProperty(p,at));
            }

            try {
                p.add(cb.build());
            } catch (StructureException e) {
                e.printStackTrace();
            }
        }
        else {
            Interface.Builder ib = p.getInterfaceBuilder();
            ib = ib.withName(name[name.length-1]);
            attribute = source.split(";");
            for (int i=1; i<attribute.length; i++)
            {
                String at = attribute[i];
                if (at.indexOf('(')>-1) {
                    if (createMethod(p,at)!=null) ib.withMethod(createMethod(p,at));
                }
                else
                    if (createProperty(p,at)!=null) ib.withProperty(createProperty(p,at));
            }
            try {
                p.add(ib.build());
            } catch (StructureException e) {
                e.printStackTrace();
            }
        }

    }
}