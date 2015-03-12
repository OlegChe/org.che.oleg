package server;

public class StatEntry {
    private final String url;
    private final String ip;
    private final Long timestamp;
    private final Integer receiveByte;
    private final Integer sentByte;
    private final Integer throughput;

    public StatEntry(String url, String ip, Long timestamp, Integer receiveByte, Integer sentByte, Integer throughput) {
        this.url = url;
        this.ip = ip;
        this.timestamp = timestamp;
        this.receiveByte = receiveByte;
        this.sentByte = sentByte;
        this.throughput = throughput;
    }

    public String getUrl() {
        return url;
    }

    public String getIp() {
        return ip;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Integer getReceiveByte() {
        return receiveByte;
    }
    
    public Integer getSentByte() {
        return sentByte;
    }
    
    public Integer getThroughput() {
        return throughput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatEntry statEntry = (StatEntry) o;

        if (receiveByte != null ? !receiveByte.equals(statEntry.receiveByte) : statEntry.receiveByte != null)
            return false;
        if (sentByte != null ? !sentByte.equals(statEntry.sentByte) : statEntry.sentByte != null)
            return false;
        if (timestamp != null ? !timestamp.equals(statEntry.timestamp) : statEntry.timestamp != null) return false;
        if (ip != null ? !ip.equals(statEntry.ip) : statEntry.ip != null) return false;
        if (url != null ? !url.equals(statEntry.url) : statEntry.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (receiveByte != null ? receiveByte.hashCode() : 0);
        result = 31 * result + (sentByte != null ? sentByte.hashCode() : 0);
        result = 31 * result + (throughput != null ? throughput.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatEntry{");
        sb.append("url='").append(url).append('\'');
        sb.append(", ip='").append(ip).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", receiveByte=").append(receiveByte);
        sb.append(", sentByte=").append(sentByte);
        sb.append(", throughput=").append(throughput);
        sb.append('}');
        return sb.toString();
    }
}
