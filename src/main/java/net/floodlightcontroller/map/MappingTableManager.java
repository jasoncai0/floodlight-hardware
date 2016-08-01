/**
 *    Copyright   2016,  network  research  laboratory  662,INC.
 *    Originally   created  by  sun  xiao  tian
 */
package net.floodlightcontroller.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.OFVlanVidMatch;
import org.projectfloodlight.openflow.types.U64;
import org.projectfloodlight.openflow.types.VlanVid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.IPv6;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.ForwardingBase;
import net.floodlightcontroller.routing.IRoutingDecision;
import net.floodlightcontroller.util.FlowModUtils;


public  class MappingTableManager extends ForwardingBase implements  IMappingTableManagerService,IOFMessageListener,
IFloodlightModule  {
	protected static final Logger log = LoggerFactory.getLogger(MappingTableManager.class);
	protected IFloodlightProviderService floodlightProvider;
	protected IRestApiService restApi;
	protected static Logger logger;
	protected static Connection  connection;
	protected Statement    statement;
	protected ResultSet     resultset;


	public  static void  ConnectDatabase() throws SQLException, ClassNotFoundException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String  url = "jdbc:mysql://127.0.0.1:3306/xtrcontroller";
			String user = "root";
			String password = "wjcai376";
		    connection  =  DriverManager.getConnection(url, user, password);
			if(!connection.isClosed())
				log.info("******Succeeded  connnecting  to  the  database!******");
			/*
			Statement stmt= connection.createStatement();
			String sql = "";
			stmt.executeUpdate(sql);
			String sqlquery = "";
			ResultSet rs = stmt.executeQuery(sqlquery);
			while(rs.next()){
				rs.getString("ddsdd");

			}
			*/

		}catch(ClassNotFoundException e) {
			log.info("Sorry,cant't  find  the  Driver!");
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	@Override
	public  String   getAllRules() {
		MapRule mr=null;
		String ret="";
		try{
			log.info("******eid"+"++++"+"rloc******");
			statement  =  connection.createStatement();
			String sql  =  "select  * from  eid_to_rloc  ";
			resultset =  statement.executeQuery(sql);
			while(resultset.next()){
				mr = new MapRule(resultset.getString("eid"),resultset.getString("rloc"));
				ret +=mr.getMapRule();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}



	@Override
	public  String  getRules( String  eid)  {
		String  dst_rloc="";
		try{
			statement  =  connection.createStatement();
			StringBuilder  sql  =  new  StringBuilder("select  * from  eid_to_rloc  where eid="+"'"+eid+"'");
			log.info("SXT_DBG:******"+sql.toString());
			resultset =  statement.executeQuery(sql.toString());
			if(resultset.next()){
				dst_rloc  = resultset.getString("rloc");
				log.info("SXT_DBG:***dst_rloc:"+dst_rloc+"***");
			}else{
				dst_rloc =  null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return  dst_rloc;
	}
	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		switch (msg.getType()) {
		case PACKET_IN:
			log.info("SXT_DBG: ----receive  packet_in -----");
			return    handlePacketInMessage(sw, (OFPacketIn) msg, cntx);
		default:
			break;
		}
		return Command.CONTINUE;
	}


	@Override
	public String getName() {
		return "map";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;  // no dependency for non-packet in
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return false;  // no dependency for non-packet in
	}


	public Command  handlePacketInMessage(IOFSwitch sw,OFPacketIn pi,FloodlightContext cntx) {
		OFPort inPort = (pi.getVersion().compareTo(OFVersion.OF_12) < 0 ? pi.getInPort() : pi.getMatch().get(MatchField.IN_PORT));
		IPv6Address   src_eid =  pi.getMatch().get(MatchField.IPV6_SRC);//"ipv6_src"  instead  of    source  edge  host  IPv6  Address
	    IPv6Address   dst_eid = pi.getMatch().get(MatchField.IPV6_DST);
	    IPv6Address   src_rloc = pi.getMatch().get(MatchField.IPV6_ND_TARGET);//"ipv6_nd_target"  instead of  MagicRouter's  IPv6 Address
	    log.info("sxt_port:"+inPort.toString());
	    log.info("sxt_src_eid:"+src_eid.toString());
	    log.info("sxt_dst_eid:"+dst_eid.toString());
	    log.info("sxt_src_rloc:"+src_rloc.toString());
		try{
			statement  =  connection.createStatement();
			//StringBuilder  sql  = new  StringBuilder( "replace  into  eid_to_rloc values("+src_eid.toString()+","+src_rloc.toString()+")");
			//statement.executeUpdate(sql.toString());
			//log.info(sql.toString());
			String  sql  =  "replace  into  eid_to_rloc values('fe80::231:32ff:fe33:6090','fe80::1:231:32ff:fe33:6091')";
			statement.executeUpdate(sql);
			log.info("replace  into  eid_to_rloc values('fe80::231:32ff:fe33:6090','fe80::1:231:32ff:fe33:6091')");
		}catch(Exception e){
			e.printStackTrace();
		}
		if(dst_eid.toString() == null){
			log.info("SXT_DBG:********dst_eid=null,will    package  src_eid ->src_rloc   doFlowMod****");
			doFlowMod(sw, pi, cntx,src_eid,src_rloc);
		}else{
			if(getRules(dst_eid.toString()) != null){
				log.info("SXT_DBG:********query  dst_rloc  success,will   doFlowMod****");
				doFlowMod(sw, pi, cntx,dst_eid,IPv6Address.of(getRules(dst_eid.toString())));
				//OFPort   _outPort   =  OFPort.of(0);//Defaulted   NetMagicPro    outPort   is  zero
				//pushPacket(sw, pi, _outPort, true, cntx);//packet_out
			}else{
				log.info("SXT_DBG: *******query  dst_rloc  failed,  will   drop  message*******");
				doDropFlow(sw, pi, null, cntx);
			}
		}
		return Command.CONTINUE;

	}

	protected void doFlowMod(IOFSwitch sw, OFPacketIn pi, FloodlightContext cntx,IPv6Address  eid,IPv6Address  rloc) {
		log.info("SXT_DBG:  ******start  doFLowMod******");
		Match m = createMatchFromPacket0(sw, eid,rloc,cntx);
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();
		List<OFAction> actions = new ArrayList<OFAction>();
		U64 cookie = AppCookie.makeCookie(FORWARDING_APP_ID, 0);//*********
		OFPort   outPort   =  OFPort.of(0);//Defaulted   NetMagicPro    outPort   is  zero
		//log.info("doflowmod");
		fmb.setCookie(cookie)
		.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
		.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
		.setBufferId(OFBufferId.NO_BUFFER)
		.setOutPort(outPort)
		.setMatch(m)
		.setPriority(FLOWMOD_DEFAULT_PRIORITY);

		FlowModUtils.setActions(fmb, actions, sw);
		try {
			if (log.isDebugEnabled()) {
				log.debug("write  flow-mod sw={} match={} flow-mod={}",
						new Object[] { sw, m, fmb.build() });
			}
			boolean dampened = messageDamper.write(sw, fmb.build());
			log.info("SXT_DBG:******Send  flowmod(add)  message  successful*****");
			log.debug("OFMessage dampened: {}", dampened);
		} catch (IOException e) {
			log.error("Failure writing  flow mod", e);
		}
	}

	protected Match createMatchFromPacket0(IOFSwitch sw,IPv6Address _eid, IPv6Address _rloc,FloodlightContext cntx) {
		log.info("SXT_DBG: ******createMatchFromPacket0******");
		Match.Builder mb = sw.getOFFactory().buildMatch();
		IPv6Address src_eid = _eid;
		IPv6Address dst_rloc = _rloc;
		mb.setExact(MatchField.ETH_TYPE, EthType.IPv6)
		.setExact(MatchField.IPV6_SRC, src_eid)
		.setExact(MatchField.IPV6_DST, dst_rloc);
		return mb.build();
	}

	protected void doDropFlow(IOFSwitch sw, OFPacketIn pi, IRoutingDecision decision, FloodlightContext cntx) {
		OFPort inPort = (pi.getVersion().compareTo(OFVersion.OF_12) < 0 ? pi.getInPort() : pi.getMatch().get(MatchField.IN_PORT));
		Match m = createMatchFromPacket(sw, inPort, cntx);
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd(); // this will be a drop-flow; a flow that will not output to any ports
		List<OFAction> actions = new ArrayList<OFAction>(); // set no action to drop
		U64 cookie = AppCookie.makeCookie(FORWARDING_APP_ID, 0);
		//log.info("Droppingggg");
		fmb.setCookie(cookie)
		.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
		.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
		.setBufferId(OFBufferId.NO_BUFFER)
		.setMatch(m)
		.setPriority(FLOWMOD_DEFAULT_PRIORITY);

		FlowModUtils.setActions(fmb, actions, sw);

		try {
			if (log.isDebugEnabled()) {
				log.debug("write drop flow-mod sw={} match={} flow-mod={}",
						new Object[] { sw, m, fmb.build() });
			}
			boolean dampened = messageDamper.write(sw, fmb.build());
			log.info("SXT_DBG:******Send   flowmod(drop) message  successful !******");
			log.debug("OFMessage dampened: {}", dampened);
		} catch (IOException e) {
			log.error("Failure writing drop flow mod", e);
		}
	}

	protected Match createMatchFromPacket(IOFSwitch sw, OFPort inPort, FloodlightContext cntx) {
		// The packet in match will only contain the port number.
		// We need to add in specifics for the hosts we're routing between.
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		VlanVid vlan = VlanVid.ofVlan(eth.getVlanID());
		MacAddress srcMac = eth.getSourceMACAddress();
		MacAddress dstMac = eth.getDestinationMACAddress();

		Match.Builder mb = sw.getOFFactory().buildMatch();
		mb.setExact(MatchField.IN_PORT, inPort);

		if (FLOWMOD_DEFAULT_MATCH_MAC) {
			mb.setExact(MatchField.ETH_SRC, srcMac)
			.setExact(MatchField.ETH_DST, dstMac);
		}

		if (FLOWMOD_DEFAULT_MATCH_VLAN) {
			if (!vlan.equals(VlanVid.ZERO)) {
				mb.setExact(MatchField.VLAN_VID, OFVlanVidMatch.ofVlanVid(vlan));
			}
		}

		// TODO Detect switch type and match to create hardware-implemented flow
		if (eth.getEtherType() == EthType.IPv4) { /* shallow check for equality is okay for EthType */
			IPv4 ip = (IPv4) eth.getPayload();
			IPv4Address srcIp = ip.getSourceAddress();
			IPv4Address dstIp = ip.getDestinationAddress();

			if (FLOWMOD_DEFAULT_MATCH_IP_ADDR) {
				mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
				.setExact(MatchField.IPV4_SRC, srcIp)
				.setExact(MatchField.IPV4_DST, dstIp);
			}

			if (FLOWMOD_DEFAULT_MATCH_TRANSPORT) {
				/*
				 * Take care of the ethertype if not included earlier,
				 * since it's a prerequisite for transport ports.
				 */
				if (!FLOWMOD_DEFAULT_MATCH_IP_ADDR) {
					mb.setExact(MatchField.ETH_TYPE, EthType.IPv4);
				}

				if (ip.getProtocol().equals(IpProtocol.TCP)) {
					TCP tcp = (TCP) ip.getPayload();
					mb.setExact(MatchField.IP_PROTO, IpProtocol.TCP)
					.setExact(MatchField.TCP_SRC, tcp.getSourcePort())
					.setExact(MatchField.TCP_DST, tcp.getDestinationPort());
				} else if (ip.getProtocol().equals(IpProtocol.UDP)) {
					UDP udp = (UDP) ip.getPayload();
					mb.setExact(MatchField.IP_PROTO, IpProtocol.UDP)
					.setExact(MatchField.UDP_SRC, udp.getSourcePort())
					.setExact(MatchField.UDP_DST, udp.getDestinationPort());
				}
			}
		} else if (eth.getEtherType() == EthType.ARP) { /* shallow check for equality is okay for EthType */
			mb.setExact(MatchField.ETH_TYPE, EthType.ARP);
		} else if (eth.getEtherType() == EthType.IPv6) {
			IPv6 ip = (IPv6) eth.getPayload();
			IPv6Address srcIp = ip.getSourceAddress();
			IPv6Address dstIp = ip.getDestinationAddress();

			if (FLOWMOD_DEFAULT_MATCH_IP_ADDR) {
				mb.setExact(MatchField.ETH_TYPE, EthType.IPv6)
				.setExact(MatchField.IPV6_SRC, srcIp)
				.setExact(MatchField.IPV6_DST, dstIp);
			}

			if (FLOWMOD_DEFAULT_MATCH_TRANSPORT) {
				/*
				 * Take care of the ethertype if not included earlier,
				 * since it's a prerequisite for transport ports.
				 */
				if (!FLOWMOD_DEFAULT_MATCH_IP_ADDR) {
					mb.setExact(MatchField.ETH_TYPE, EthType.IPv6);
				}

				if (ip.getNextHeader().equals(IpProtocol.TCP)) {
					TCP tcp = (TCP) ip.getPayload();
					mb.setExact(MatchField.IP_PROTO, IpProtocol.TCP)
					.setExact(MatchField.TCP_SRC, tcp.getSourcePort())
					.setExact(MatchField.TCP_DST, tcp.getDestinationPort());
				} else if (ip.getNextHeader().equals(IpProtocol.UDP)) {
					UDP udp = (UDP) ip.getPayload();
					mb.setExact(MatchField.IP_PROTO, IpProtocol.UDP)
					.setExact(MatchField.UDP_SRC, udp.getSourcePort())
					.setExact(MatchField.UDP_DST, udp.getDestinationPort());
				}
			}
		}
		return mb.build();
	}
	// IFloodlightModule
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		log.info("MAP GET MODULE SERVICE");
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IMappingTableManagerService.class);
		return  l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		log.info("MAP GET SERVICE IMPL");

		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		// We are the class that implements the service
		m.put(IMappingTableManagerService.class, this);
		return  m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		log.info("MAP GET MODULE DEPENDENCIES");

		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		return l;
	}


	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		logger = LoggerFactory.getLogger(MappingTableManager.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		restApi.addRestletRoutable(new  MappingTableManagerWebRoutable());
		try {
			ConnectDatabase();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public net.floodlightcontroller.core.IListener.Command processPacketInMessage(
			IOFSwitch sw, OFPacketIn pi, IRoutingDecision decision,
			FloodlightContext cntx) {
		// TODO Auto-generated method stub
		return null;
	}
}
