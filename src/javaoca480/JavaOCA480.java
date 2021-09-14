/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaoca480;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;
import org.opennebula.client.template.Template;
import org.opennebula.client.vm.VirtualMachine;
import viniciusfacco.string.XmlFormatter;

/**
 *
 * @author Vinicius
 */
public class JavaOCA480 {
    
    Client oneClient;
    private String user;
    private String password;
    private String server;
    private boolean connected;
    
    public JavaOCA480(){
        connected = false;
    }
    
    public void set_user(String u){
        user = u;
    }
    
    public void set_password(String p){
        password = p;
    }
    
    public void set_server(String s){
        server = s;
    }
    
    public boolean connected(){
        return connected;
    }
    
    public void connect(){
        try {
            oneClient = new Client(user + ":" + password, "http://" + server + ":2633/RPC2");
        } catch (ClientConfigurationException ex) {
            Logger.getLogger(JavaOCA480.class.getName()).log(Level.SEVERE, null, ex);
        }
        connected = oneClient.get_version().getMessage() != null;
    }
    
    public int new_host(String name){
        OneResponse rc = Host.allocate(oneClient, name, "kvm", "kvm", "dummy");
        if( rc.isError() ){
            System.out.println("Falha ao criar Host!" + "\n" + rc.getErrorMessage());
            return 0;
        }
        else {
            return Integer.parseInt(rc.getMessage());
        }
    }
    
    public int new_vm(int template){
        Template vmtemplate = new Template(template, oneClient);
        OneResponse rc = vmtemplate.instantiate();
        if (rc.isError()){
            return 0;
        } else {
            return Integer.parseInt(rc.getMessage());
        }
    }
    
    public String get_xml_host(int id){
        // First of all, a Client object has to be created.
        // Here the client will try to connect to OpenNebula using the default
        // options: the auth. file will be assumed to be at $ONE_AUTH, and the
        // endpoint will be set to the environment variable $ONE_XMLRPC.
        
        XmlFormatter xmlfor = new XmlFormatter();
        String msg;

        Host host = new Host(id, oneClient);
        OneResponse rc = host.info();
        if(rc.isError())
                try {
                    return "Error";
        } catch (Exception ex) {
            Logger.getLogger(JavaOCA480.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.out.println(rc.getMessage() + "\n");
        msg = rc.getMessage();
        
//        try {
//            System.out.println("Requisição realizada através de XML-RPC:\n" + xmlrpcrequest());
//        } catch (XmlRpcException | MalformedURLException ex) {
//            Logger.getLogger(JavaOCA480.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        return xmlfor.format(msg);
        
    }
    
    public String get_xml_vm(int id){
        XmlFormatter xmlfor = new XmlFormatter();
        String msg;

        VirtualMachine vm = new VirtualMachine(id, oneClient);
        OneResponse rc = vm.info();
        if(rc.isError())
                try {
                    return "Error";
        } catch (Exception ex) {
            Logger.getLogger(JavaOCA480.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.out.println(rc.getMessage() + "\n");
        msg = rc.getMessage();
        
//        try {
//            System.out.println("Requisição realizada através de XML-RPC:\n" + xmlrpcrequest());
//        } catch (XmlRpcException | MalformedURLException ex) {
//            Logger.getLogger(JavaOCA480.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        return xmlfor.format(msg);        
    }
    
    public String get_xml_hostpool(){
        XmlFormatter xmlfor = new XmlFormatter();
        String msg;
        HostPool hp = new HostPool(oneClient);
        msg = hp.info().getMessage();
        return xmlfor.format(msg);
    }
    
    public String xmlrpcrequest() throws XmlRpcException, MalformedURLException{
        XmlRpcClient client;
	XmlRpcClientConfigImpl config;
	URL headnode;
	String SESSION = "oneadmin:oneadmin";
        
        client = new XmlRpcClient();
	headnode = new URL("http://10.210.7.130:2633/RPC2");
	config = new XmlRpcClientConfigImpl();
	config.setServerURL(headnode);
        client.setConfig(config);
        
        Object[] params = {SESSION, 3};
        Object result = client.execute("one.host.info",params);
	return (String)((Object[])result)[1];
    }
    
}
