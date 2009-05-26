package com.amalto.core.objects.routing.v2.ejb;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.xml.transform.TransformerException;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocal;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


/**
 * @author bgrieder
 * 
 * @ejb.bean name="RoutingEngineV2Ctrl"
 *           display-name="RoutingEngineV2Ctrl"
 *           description="Routing Engine"
 *           jndi-name="amalto/remote/core/routingenginev2ctrl"
 * 		  local-jndi-name = "amalto/local/core/routingenginev2ctrl"
 *           type="Stateless"
 *           view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 */
public class RoutingEngineV2CtrlBean implements SessionBean, TimedObject {
  

	
	/**
	 * Delay after which the routing rule is executed when the rule must be executed "now"
	 */
	private final static long DELAY = 15;
		

	private final static String LOGGING_EVENT = "logging_event";
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'm'SSS");
	
	
	private SessionContext context;
		
    
    /**
     * DocumentCtrlBean.java
     * Constructor
     * 
     */
    public RoutingEngineV2CtrlBean() {
        super();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
		    	org.apache.log4j.Logger.getLogger(this.getClass()).info("Shutdown detected. Stopping the Routing Engine");
		    	RoutingEngineV2POJO.getInstance().setStatus(RoutingEngineV2POJO.STOPPED);
			}
		});
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    	context = ctx;
    }
    

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() throws EJBException, RemoteException {
    }
    
    /**
     * Create method
     * @ejb.create-method  view-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {}
    
    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {}
    
    
    /**
     * Routes a document
     * @return the list of routing rules PKs that matched
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RoutingRulePOJOPK[] route(ItemPOJOPK itemPOJOPK) throws XtentisException {
    	    	
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("route() ROUTING Item "+itemPOJOPK.getUniqueID());
    	
		//check subscription engine state			
		if (RoutingEngineV2POJO.getInstance().getStatus()==RoutingEngineV2POJO.STOPPED) {
			//the routing engine is stopped of in Error - throw an Exception
			String err = "Unable to route the Item '"+itemPOJOPK.getUniqueID()+"'. The Subscription Engine is stopped.";
			if (LOGGING_EVENT.equals(itemPOJOPK.getConceptName()))
				org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+err);
			else
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
			throw new XtentisException(err);
		}

		
		//Retrieve the Routing Rule Controller
		RoutingRuleCtrlLocal routingRuleCtrl = null;		
		try {
			routingRuleCtrl = Util.getRoutingRuleCtrlLocal();
		} catch (NamingException e) {
			String err = "Unable to route the Item '"+itemPOJOPK.getUniqueID()+"'. "+"The routing rules controller cannot be found: "+e.getMessage();
			if (LOGGING_EVENT.equals(itemPOJOPK.getConceptName()))
				org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+err,e);
			else
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(err);
		} catch (CreateException e) {
			String err = "Unable to route the Item '"+itemPOJOPK.getUniqueID()+"'. "+"The routing rules controller cannot be created: "+e.getMessage();
			if (LOGGING_EVENT.equals(itemPOJOPK.getConceptName()))
				org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
			else
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(err);
		}
		
		//Retrieve the Routing Rule Controller
		RoutingOrderV2CtrlLocal routingOrderCtrl = null;
		try {
			routingOrderCtrl = Util.getRoutingOrderV2CtrlLocal();
		} catch (NamingException e) {
			String err = "Unable to route the Item '"+itemPOJOPK.getUniqueID()+"'. "+"The Routing Orders controller cannot be found: "+e.getMessage();
			if (LOGGING_EVENT.equals(itemPOJOPK.getConceptName()))
				org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+err,e);
			else
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(err);		
		} catch (CreateException e) {
			String err = "Unable to route the Item '"+itemPOJOPK.getUniqueID()+"'. "+"The Routing Orders controller cannot be created: "+e.getMessage();
			if (LOGGING_EVENT.equals(itemPOJOPK.getConceptName()))
				org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
			else
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(err);
		}
		
		//Retrieve the Item Controller
		ItemCtrl2Local itemCtrl = null;
		try {
			itemCtrl = Util.getItemCtrl2Local();
		} catch (NamingException e) {
			String err = "Unable to route the Item '"+itemPOJOPK.getUniqueID()+"'. "+"The Items controller cannot be found: "+e.getMessage();
			if (LOGGING_EVENT.equals(itemPOJOPK.getConceptName()))
				org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+err,e);
			else
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(err);
		} catch (CreateException e) {
			String err = "Unable to route the Item '"+itemPOJOPK.getUniqueID()+"'. "+"The items controller cannot be created: "+e.getMessage();
			if (LOGGING_EVENT.equals(itemPOJOPK.getConceptName()))
				org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
			else
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(err);
		}

		
		//The cached ItemPOJO - will only be retrieved if needed: we have expressions on the routing rules
		ItemPOJO itemPOJO = null;
    	//Rules that matched
		ArrayList<RoutingRulePOJOPK> routingRulesThatMatched = new ArrayList<RoutingRulePOJOPK>();
		

		//loop over the known rules
		Collection<RoutingRulePOJOPK> routingRulePOJOPKs = routingRuleCtrl.getRoutingRulePKs("*");
		for (Iterator<RoutingRulePOJOPK> iter = routingRulePOJOPKs.iterator(); iter.hasNext(); ) {
			RoutingRulePOJOPK routingRulePOJOPK = iter.next();
			RoutingRulePOJO routingRule = routingRuleCtrl.getRoutingRule(routingRulePOJOPK);
			//check if document type is OK
			if (! "*".equals(routingRule.getConcept())) {
				String docType = itemPOJOPK.getConceptName();
				if (! docType.equals(routingRule.getConcept())) continue;
			}
			//check if all routing rule expression matches - null: always matches
			boolean matches = true;
			Collection<RoutingRuleExpressionPOJO> routingExpressions = routingRule.getRoutingExpressions();
			if (routingExpressions != null) {
				for (Iterator<RoutingRuleExpressionPOJO> iterator = routingExpressions.iterator(); iterator.hasNext(); ) {
					//retrieve the itemPOJO if not already done
					if (itemPOJO==null) {
						itemPOJO = itemCtrl.getItem(itemPOJOPK);
					}
					//Match the rule
					RoutingRuleExpressionPOJO rrePOJO = iterator.next();
					if (! ruleExpressionMatches(itemPOJO, rrePOJO)) {
						matches = false;
						break;
					}
				}
			}
			if (! matches) continue;
		
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
				"route() Routing Rule MATCH '"+routingRulePOJOPK.getUniqueId()+"' for item '"+itemPOJOPK.getUniqueID()+"'"
			);
			
			//increment matching routing rules counter
			routingRulesThatMatched.add(routingRulePOJOPK);

			//create the routing Order
			Date now = new Date();
			String name = itemPOJOPK.getUniqueID()+"-"+sdf.format(now);
			ActiveRoutingOrderV2POJO routingOrderPOJO = new ActiveRoutingOrderV2POJO(
				name,
				routingRule.isSynchronous() ? now.getTime() : now.getTime()+DELAY,
				itemPOJOPK,
				"Routing of '"+itemPOJOPK.getUniqueID()+"' to service '"+routingRule.getServiceJNDI().replaceFirst("amalto/local/service/", "")+"'"
					+" triggered by rule '"+routingRulePOJOPK.getUniqueId()+"'",
				routingRule.getServiceJNDI(),
				routingRule.getParameters()
			);
				
			//there is one case where everything is run now
			if (routingRule.isSynchronous()) {
				if (RoutingEngineV2POJO.getInstance().getStatus()==RoutingEngineV2POJO.RUNNING) {
					routingOrderCtrl.executeSynchronously(routingOrderPOJO);
					continue;
				}
			}
			
			//save the routing order for later routing
			routingOrderCtrl.putRoutingOrder(routingOrderPOJO);
			
			//make sure that the thread is started
			if (RoutingEngineV2POJO.getInstance().getStatus()==RoutingEngineV2POJO.RUNNING) {
				Collection<Timer> timers = context.getTimerService().getTimers();
				if ((timers==null) || (timers.size()<1))
					createTimer(new RoutingEngineV2POJOPK(RoutingEngineV2POJO.getInstance().getPK()), RoutingEngineV2POJO.getInstance().getMinRunPeriodMillis());
			}	
			
		}//for routing Rules
		
		
		if (routingRulesThatMatched.size() == 0) {
    	    String err = "Unable to find a routing rule for document "	+itemPOJOPK.getUniqueID();
			if (LOGGING_EVENT.equals(itemPOJOPK.getConceptName()))
				org.apache.log4j.Logger.getLogger(this.getClass()).info(err);
			else
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
    	}

		return routingRulesThatMatched.toArray(new RoutingRulePOJOPK[routingRulesThatMatched.size()]);
    }
    
    
    /**
     * Check that a rule actually matches a document
     * @param itemPOJO
     * @param exp
     * @return true if it matches
     * @throws XtentisException
     */
    private boolean ruleExpressionMatches(ItemPOJO itemPOJO, RoutingRuleExpressionPOJO exp) throws XtentisException{
    	//org.apache.log4j.Logger.getLogger(this.getClass()).debug("ruleExpressionMatches() "+exp.getXpath()+"  "+exp.getValue());
    	
    	String content = null;
    	int contentInt,expInt;
    	
    	try {
    		content = Util.getFirstTextNode(itemPOJO.getProjection(), exp.getXpath());
    	} catch (TransformerException e) {
    		String err = "Subscription rule expression match: unable extract xpath '"+exp.getXpath()+"' from Item '"+itemPOJO.getItemPOJOPK().getUniqueID()+"': "+e.getLocalizedMessage();
    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    		throw new XtentisException(err);
    	}
    	
    	switch (exp.getOperator()) {
    		case RoutingRuleExpressionPOJO.CONTAINS:
    			if (content!=null) return (content.indexOf(exp.getValue())>-1);
    			return false;
    		case RoutingRuleExpressionPOJO.EQUALS:
    			if (content!=null) return (content.equals(exp.getValue()));
    			return false;
    		case RoutingRuleExpressionPOJO.GREATER_THAN:
    			try { expInt = Integer.parseInt(exp.getValue());} catch (Exception e) {return false;}
    			try { contentInt = Integer.parseInt(content);} catch (Exception e) {return false;}
    			if (content!=null) return (expInt > contentInt);
    			return false;
    		case RoutingRuleExpressionPOJO.GREATER_THAN_OR_EQUAL:
    			try { expInt = Integer.parseInt(exp.getValue());} catch (Exception e) {return false;}
    			try { contentInt = Integer.parseInt(content);} catch (Exception e) {return false;}
    			if (content!=null) return (expInt >= contentInt);
    			return false;
    		case RoutingRuleExpressionPOJO.IS_NOT_NULL:
    			return (content != null);
    		case RoutingRuleExpressionPOJO.IS_NULL:
    			return (content == null);
    		case RoutingRuleExpressionPOJO.LOWER_THAN:
    			try { expInt = Integer.parseInt(exp.getValue());} catch (Exception e) {return false;}
    			try { contentInt = Integer.parseInt(content);} catch (Exception e) {return false;}
    			if (content!=null) return (expInt < contentInt);
    			return false;
    		case RoutingRuleExpressionPOJO.LOWER_THAN_OR_EQUAL:
    			try { expInt = Integer.parseInt(exp.getValue());} catch (Exception e) {return false;}
    			try { contentInt = Integer.parseInt(content);} catch (Exception e) {return false;}
    			if (content!=null) return (expInt <= contentInt);
    			return false;
    		case RoutingRuleExpressionPOJO.MATCHES:
    			if (exp.getValue() == null) return false;
    			if (content!=null) return (content.matches(exp.getValue()));
    			return false;
    		case RoutingRuleExpressionPOJO.NOT_EQUALS:
    			if (content!=null) return (! content.equals(exp.getValue()));
    			return false;    			
    		case RoutingRuleExpressionPOJO.STARTSWITH:
    			if (exp.getValue() == null) return false;
    			if (content!=null) return (content.startsWith(exp.getValue()));
    			return false;    			
    		default:
    			return false;
    	}
    }
    
    
   
    
    /*****************************************************************
     *  Engine LifeCycle
    *****************************************************************/

    
    /**
     * Starts/restarts the router
     * @throws XtentisException
     * 
     * @ejb.interface-method  view-type = "both"
     * @ejb.facade-method 
     */
    public void start() throws XtentisException{       
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("start() ");
        RoutingEngineV2POJO.getInstance().setStatus(RoutingEngineV2POJO.RUNNING);
		//make sure that the thread is started
		Collection<Timer> timers = context.getTimerService().getTimers();
		if ((timers==null) || (timers.size()<1))
			createTimer(new RoutingEngineV2POJOPK(RoutingEngineV2POJO.getInstance().getPK()), RoutingEngineV2POJO.getInstance().getMinRunPeriodMillis());	
    }
    

    
    /**
     * Stops the routing queue
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void stop() throws XtentisException{       
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("stop() ");
        RoutingEngineV2POJO.getInstance().setStatus(RoutingEngineV2POJO.STOPPED);
    }
  
    
    /**
     * Toggle suspend a routing queue
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void suspend(boolean suspend) throws XtentisException{       
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("suspend() "+suspend);
        if (suspend)
        	RoutingEngineV2POJO.getInstance().setStatus(RoutingEngineV2POJO.SUSPENDED);
        else
        	RoutingEngineV2POJO.getInstance().setStatus(RoutingEngineV2POJO.RUNNING);
    }
    
    /**
     * Toggle suspend a routing queue
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public int getStatus() throws XtentisException{       
      	return RoutingEngineV2POJO.getInstance().getStatus();
    }

    
    
    /*****************************************************************
     *  T I M E R
    *****************************************************************/
    
    /**
     * 
     * @param intervalDuration
     * @return a TimerHandle
     */
    private TimerHandle createTimer(RoutingEngineV2POJOPK routingEnginePOJOPK, long ms) {
    	ms  = Math.max(ms, RoutingEngineV2POJO.getInstance().getMinRunPeriodMillis());
        TimerService timerService =  context.getTimerService();
        Timer timer = timerService.createTimer(ms,routingEnginePOJOPK);  
        TimerHandle th = timer.getHandle();
        return th;
    }
    
        
	/* (non-Javadoc)
	 * @see javax.ejb.TimedObject#ejbTimeout(javax.ejb.Timer)
	 */
	public void ejbTimeout(Timer timer) {
		
		//RoutingEngineV2POJOPK routingEngine = (RoutingEngineV2POJOPK) timer.getInfo();
		//for the moment we have a single static routing engine
		RoutingEngineV2POJO routingEngine = RoutingEngineV2POJO.getInstance();
		
		org.apache.log4j.Logger.getLogger(this.getClass()).trace(
			"ejbTimeout() Running Engine on Thread id "+Thread.currentThread().getId()
		);

		//if routing engine is not running stop here
		if (routingEngine.getStatus() != RoutingEngineV2POJO.RUNNING) return;
		
		//capture start time to keep intervals regular as much as we can
		long startTime = System.currentTimeMillis();
		//extra time - if we want to increase the duration between two runs to provide throttling
		long extraTime = 0;
		
		//fetch routing Order Controller
		RoutingOrderV2CtrlLocal routingOrderCtrl = null;
		try {
			routingOrderCtrl = Util.getRoutingOrderV2CtrlLocal();
		} catch (NamingException e) {
			//mark the engine as stopped
			routingEngine.setStatus(RoutingEngineV2POJO.STOPPED);
			String err = "Unable to execute the scheduled routing orders. The Routing Orders controller cannot be found: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			return;
		} catch (CreateException e) {
			//mark the engine as stopped
			routingEngine.setStatus(RoutingEngineV2POJO.STOPPED);
			String err = "Unable to execute the scheduled routing orders. The Routing Orders cannot be created: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			return;
		}
		
		
		//garbage collect considered dead routing orders
		if (startTime-routingEngine.getLastDeadRoutingOrdersSweep()>routingEngine.getMaxExecutionTimeMillis()/2) {
			//update the last run time
			routingEngine.setLastDeadRoutingOrdersSweep(startTime);
			//catch the max execution time
			long maxExecutionTime = routingEngine.getMaxExecutionTimeMillis();
			//search routing orders that must be run
			ActiveRoutingOrderV2POJO[] routingOrders = null;
			try {
				routingOrders = routingOrderCtrl.findDeadRoutingOrders(startTime-maxExecutionTime, 100);
			} catch (XtentisException e) {
				//mark the engine as stopped
				//DISABLED: we can go on anyway routingEngine.setStatus(RoutingEngineV2POJO.STOPPED);
				String err="Unable to fetch dead routing orders: "+e.getMessage();
				org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE "+err,e);
			}
			//move the caught ones to error
			if (routingOrders!=null) {
				for (int i = 0; i < routingOrders.length; i++) {
					String message = "Routing Order "+routingOrders[i].getAbstractRoutingOrderPOJOPK().getUniqueId()+" has exceeded the max execution time of "+maxExecutionTime+" seconds.";
			    	//Log
					org.apache.log4j.Logger.getLogger(this.getClass()).error(message);
					//move to FAILED
					try {
						//Create Failed Routing Order
						FailedRoutingOrderV2POJO failedRO = new FailedRoutingOrderV2POJO(routingOrders[i]);
						//Remove Current active one
						routingOrderCtrl.removeRoutingOrder(routingOrders[i].getAbstractRoutingOrderPOJOPK());
						//save failed Routing Order
						failedRO.setMessage(
							failedRO.getMessage()+
							"\n---> FAILED "+sdf.format(new Date())+": "+message
						);
						routingOrderCtrl.putRoutingOrder(failedRO);
					} catch (XtentisException e) {
						String err="Unable to move dead routing order '"+routingOrders[i].getAbstractRoutingOrderPOJOPK().getUniqueId()+"' to failed queue: "+e.getMessage();
						org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE "+err,e);
					}
				}
			}
		}

		
		
		
		//get the number of simultaneous executors 
		int numberOfExecutors = routingEngine.getMaxNumberOfExecutors();
			
		//garbage Collect executors
		HashMap<String, RoutingEngineV2ExecutorPOJO> previousRunExecutors = new HashMap<String, RoutingEngineV2ExecutorPOJO>(routingEngine.getExecutors());
		HashMap<String, RoutingEngineV2ExecutorPOJO> thisRunExecutors = new HashMap<String, RoutingEngineV2ExecutorPOJO>();
		ArrayList<String> slotedRoutingOrderIDs = new ArrayList<String>();
		
		Set<String> tokens = previousRunExecutors.keySet();
		for (Iterator<String> iterator = tokens.iterator(); iterator.hasNext(); ) {
			String token = iterator.next();
			RoutingEngineV2ExecutorPOJO routingEngineV2ExecutorPOJO = previousRunExecutors.get(token);
			//check if routing order still active
			try {
				AbstractRoutingOrderV2POJOPK routingOrderPK = routingEngineV2ExecutorPOJO.getExecutingRoutingOrderPK();
    			if (routingOrderCtrl.existsRoutingOrder(routingOrderPK) != null) {
    				thisRunExecutors.put(token,routingEngineV2ExecutorPOJO);
    				slotedRoutingOrderIDs.add(routingOrderPK.getUniqueId());
    				org.apache.log4j.Logger.getLogger(this.getClass()).debug("ejbTimeout() Executor of Routing Order "+routingOrderPK.getUniqueId()+" still running");
    			} else {
    				org.apache.log4j.Logger.getLogger(this.getClass()).debug("ejbTimeout() Executor of Routing Order "+routingOrderPK.getUniqueId()+" stopped running");
    			}
			}catch (XtentisException e) {
				//log a systrace
				String err="Unable to check the existence of the Active Routing Order '"+routingEngineV2ExecutorPOJO.getExecutingRoutingOrderPK().getUniqueId()+"'" +
						". The executor cannot be reclaimed.";
				org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE "+err,e);
				return;
			}
		}
		
		//update the Routing Engine with the garbage collected executors
		routingEngine.setExecutors(thisRunExecutors);
		
		//determine the number of available tokens
		//fill in an array will all the tokens (their number)
		ArrayList<String> availableTokens = new ArrayList<String>();
		for (int i = 0; i < numberOfExecutors; i++) {
			availableTokens.add(""+i);
		}
		//remove existing executors
		tokens = thisRunExecutors.keySet();
		for (Iterator<String> iterator = tokens.iterator(); iterator.hasNext(); ) {
			String token = iterator.next();
			availableTokens.remove(token);
		}
		
		//stop here if no available slots
		if (availableTokens.size()==0) {
			org.apache.log4j.Logger.getLogger(this.getClass()).info("Subscription Engine Executors: all "+numberOfExecutors+" executors already busy");
			//catch time it took
			long duration = System.currentTimeMillis() - startTime;
			//restart process at FREQUENCY
			createTimer(new RoutingEngineV2POJOPK(routingEngine.getPK()), routingEngine.getRunPeriodMillis() - duration);
			return;
		} else {
//			org.apache.log4j.Logger.getLogger(this.getClass()).trace("ejbTimeout() Number of available executors: "+availableTokens.size());
		}

		//search routing orders that must be run
		ActiveRoutingOrderV2POJO[] routingOrders = null;
		try {
			routingOrders = routingOrderCtrl.findActiveRoutingOrders(System.currentTimeMillis(), availableTokens.size());
		} catch (XtentisException e) {
			//mark the engine as stopped
//			routingEngine.setStatus(RoutingEngineV2POJO.STOPPED); -- 2.19.1 We do not want to stop the engine anymore, just make a pause. 
//			Likely the database is overloaded -  so just wait a bit
			String err="Unable to fetch active routing orders: "+e.getMessage()+". Routing Engine will be pause for a few seconds.";
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE "+err,e);
			extraTime = 5000;
//			return;
		}
		//if nothing to process - return
		if ((routingOrders==null) || (routingOrders.length==0)) {
			//catch time it took
			long duration = System.currentTimeMillis() - startTime + extraTime;
			//restart process at FREQUENCY
			createTimer(new RoutingEngineV2POJOPK(routingEngine.getPK()), routingEngine.getRunPeriodMillis() - duration);
			return;
		}
		
		List<String> serviceJndiList = getRuntimeServiceJndiList();
		//slot new routing orders and execute them
		int tokenIndex =0;
		for (int i = 0; i < routingOrders.length && tokenIndex < availableTokens.size(); i++) {
			ActiveRoutingOrderV2POJO routingOrder = routingOrders[i];
			//make sure service JNDI is exist
			if (!serviceJndiList.contains(routingOrder.getServiceJNDI())){
				org.apache.log4j.Logger.getLogger(this.getClass()).info("Unable to lookup \""+routingOrder.getServiceJNDI()+"\", this service not bound!");
				continue;
			}
			//make sure it is not already sloted (though not yet started)
			if (slotedRoutingOrderIDs.contains(routingOrder.getItemPOJOPK().getUniqueID())) continue;
			//Not already sloted --> we will slot it and execute it
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
				"ejbTimeout() Scheduling Routing Order "+routingOrder.getName()+"on thread ID "+Thread.currentThread().getId()
				+" - "+(availableTokens.size()-i-1)+" tokens left"
			);
			//slot
			String token = availableTokens.get(tokenIndex++);
			RoutingEngineV2ExecutorPOJO executor = new RoutingEngineV2ExecutorPOJO(
																			token,
																			new RoutingEngineV2POJOPK(routingEngine.getPK()),
																			routingOrder.getAbstractRoutingOrderPOJOPK()
			);
			thisRunExecutors.put(token,executor);
			slotedRoutingOrderIDs.add(routingOrder.getItemPOJOPK().getUniqueID());
			//update routing Order
			routingOrder.setRoutingEnginePOJOPK(new RoutingEngineV2POJOPK(routingEngine.getPK()));
			routingOrder.setRoutingEngineToken(token);
			//execute
			try {
				routingOrderCtrl.executeAsynchronously(routingOrder);
			}catch (XtentisException e) {
				//mark the engine as stopped
				routingEngine.setStatus(RoutingEngineV2POJO.STOPPED);
				//log a systrace
				String err="Unable to execute the routing order '"+routingOrder.getPK().getUniqueId()+"': "+e.getMessage()
							+"The Engine will be paused for a few additional seconds";
				org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE "+err,e);
				extraTime = 5000;
				break;
			}
		}
		
		//update the Routing Engine with the scheduled or currently executing Executors -- BG I guess this should not be here.....
		routingEngine.setExecutors(thisRunExecutors);
		
		//catch time it took
		long duration = System.currentTimeMillis() - startTime + extraTime;
		
		//restart process at FREQUENCY
		createTimer(new RoutingEngineV2POJOPK(routingEngine.getPK()), routingEngine.getRunPeriodMillis() - duration);
		
	}

	private List<String> getRuntimeServiceJndiList() {
		List<String> serviceJndiList = new ArrayList<String>();
		String serviceJndiPrefix="amalto/local/service";
		try {
		InitialContext ctx = new InitialContext();
		NamingEnumeration<NameClassPair> list = ctx.list(serviceJndiPrefix);
			while (list.hasMore()) {
			    NameClassPair nc;
				
				nc = list.next();
				
			    serviceJndiList.add(serviceJndiPrefix+"/"+nc.getName());
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return serviceJndiList;
	}  
      
}