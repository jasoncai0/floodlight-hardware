package net.floodlightcontroller.map;

public class MapRule {
	public String eid = null;
	public String rloc= null;
	public String getMapRule()
	{
		return String.format("eid:%s---rloc:%s\n", eid,rloc);
	}
	public MapRule(String _eid,String _rloc)
	{
		eid = _eid;
		rloc = _rloc;
	}
}
