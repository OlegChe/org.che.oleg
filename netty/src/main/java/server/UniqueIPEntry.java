package server;

import java.util.HashSet;
import java.util.Set;

public class UniqueIPEntry {
   
    private final String ip;
    private final Integer reqCount;
    private final Long lastTimestamp;
    private final Set<String> uniqRequests;
  

    public UniqueIPEntry(String ip, Integer reqCount, Long lastTimestamp, Set<String> uniqRequests) {
       
        this.ip = ip;
        this.reqCount = reqCount;
        this.lastTimestamp = lastTimestamp;
        this.uniqRequests = uniqRequests;
       
    }

      public String getIp() {
        return ip;
    }

    public Integer getReqCount() {
        return reqCount;
    }
    
    public Long getLastTimestamp() {
        return lastTimestamp;
    }

    public Integer getUniqReqCount() {
        return uniqRequests.size();
    }
    
    public Set<String> getUniqRequests() {
        return uniqRequests;
    }
    
    public boolean contains(String reqStr) {
        return uniqRequests.contains(reqStr);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniqueIPEntry uniqIPEntry = (UniqueIPEntry) o;

        if (reqCount != null ? !reqCount.equals(uniqIPEntry.reqCount) : uniqIPEntry.reqCount != null) return false;
        if (ip != null ? !ip.equals(uniqIPEntry.ip) : uniqIPEntry.ip != null) return false;
        if (uniqRequests != null ? !uniqRequests.equals(uniqIPEntry.uniqRequests) : uniqIPEntry.uniqRequests != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int  result = (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (reqCount != null ? reqCount.hashCode() : 0);
        result = 31 * result + (uniqRequests != null ? uniqRequests.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatEntry{");
        sb.append(", ip='").append(ip).append('\'');
        sb.append(", reqCount=").append(reqCount);
        sb.append(", uniqReqCount=").append("");
        sb.append('}');
        return sb.toString();
    }
}
