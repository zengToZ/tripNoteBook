package com.zz.trip_recorder_3.tools;

import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.io.UnsupportedEncodingException;
        import java.util.LinkedHashMap;
        import java.util.Map;
        import java.util.regex.Pattern;

public class iniHelper_tools {

    // section object
    public class Section {

        private String name;

        private Map<String, Object> values = new LinkedHashMap<String, Object>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void set(String key, Object value) {
            values.put(key, value);
        }

        public Object get(String key) {
            return values.get(key);
        }

        public Map<String, Object> getValues() {
            return values;
        }


    }

    // line separator
    private String line_separator = null;

    // character coding
    private String charSet = "UTF-8";

    // section name and section content mapping
    private Map<String, Section> sections = new LinkedHashMap<String, Section>();

    public void setLineSeparator(String line_separator){
        this.line_separator = line_separator;
    }

    public void setCharSet(String charSet){
        this.charSet = charSet;
    }

    // set section object
    public void set(String section, String key, Object value) {
        Section sectionObject = sections.get(section);
        if (sectionObject == null)
            sectionObject = new Section();
        sectionObject.name = section;
        sectionObject.set(key, value);
        sections.put(section, sectionObject);
    }

    // get section
    public Section get(String section){
        return sections.get(section);
    }

    // get section and key-value
    public Object get(String section, String key) {
        return get(section, key, null);
    }

    // get section and key-value, if null return default value
    public Object get(String section, String key, String defaultValue) {
        Section sectionObject = sections.get(section);
        if (sectionObject != null) {
            Object value = sectionObject.get(key);
            if (value == null || value.toString().trim().equals(""))
                return defaultValue;
            return value;
        }
        return null;
    }

    // delete section
    public void remove(String section){
        sections.remove(section);
    }

   // delete key-value in section
    public void remove(String section,String key){
        Section sectionObject = sections.get(section);
        if(sectionObject!=null)sectionObject.getValues().remove(key);
    }


    // current file object
    private File file = null;

    public iniHelper_tools(){

    }

    public iniHelper_tools(File file) {
        this.file = file;
        initFromFile(file);
    }

    // load ini file
    public void load(File file){
        this.file = file;
        initFromFile(file);
    }

    // save to file
    public void save(File file){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            saveConfig(bufferedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // save the current file
    public void save(){
        save(this.file);
    }

    // initialize from file
    private void initFromFile(File file) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            toIniFile(bufferedReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // initialize file from bufferReader
    private void toIniFile(BufferedReader bufferedReader) {
        String strLine;
        Section section = null;
        Pattern p = Pattern.compile("^\\[.*\\]$");
        try {
            while ((strLine = bufferedReader.readLine()) != null) {
                if (p.matcher((strLine)).matches()) {
                    strLine = strLine.trim();
                    section = new Section();
                    section.name = strLine.substring(1, strLine.length() - 1);
                    sections.put(section.name, section);
                } else {
                    String[] keyValue = strLine.split("=");
                    if (keyValue.length == 2) {
                        section.set(keyValue[0], keyValue[1]);
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // save ini file
    private void saveConfig(BufferedWriter bufferedWriter){
        try {
            boolean line_spe = false;
            if(line_separator == null || line_separator.trim().equals(""))line_spe = true;
            for (Section section : sections.values()) {
                bufferedWriter.write("["+section.getName()+"]");
                if(line_spe)
                    bufferedWriter.write(line_separator);
                else
                    bufferedWriter.newLine();
                for (Map.Entry<String, Object> entry : section.getValues().entrySet()) {
                    bufferedWriter.write(entry.getKey());
                    bufferedWriter.write("=");
                    bufferedWriter.write(entry.getValue().toString());
                    if(line_spe)
                        bufferedWriter.write(line_separator);
                    else
                        bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}