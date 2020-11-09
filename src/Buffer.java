import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Buffer {
    private int size;
    private int indexPointer;
    private int maxPriority;
    private ArrayList<Request> requests;

    public Buffer(int size) {
        this.size = size;
        this.maxPriority = 3;
        indexPointer = 0;
        requests = new ArrayList<>(size);
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public Request get(int packageNumber) {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i) != null && requests.get(i).getSourceNumber() == packageNumber) {
                Request request = requests.get(i);
                requests.set(i, null);
                return request;
            }
        }
        return null;
    }

    public Request get() {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i) != null) {
                Request request = requests.get(i);
                requests.set(i, null);
                return request;
            }
        }
        return null;
    }

    public boolean addToBuffer(Request request) {
        if (requests.size() < size) {
            requests.add(indexPointer, request);
            incrementPointer();
            return true;
        } else {
            for (int i = indexPointer; i < size; i++) {
                if (requests.get(i) == null) {
                    requests.set(indexPointer, request);
                    incrementPointer();
                    return true;
                }
            }

            for (int i = 0; i < indexPointer; i++) {
                if (requests.get(i) == null) {
                    requests.set(indexPointer, request);
                    incrementPointer();
                    return true;
                }
            }

            // reject request
            int minimalPriority = maxPriority;
            int standaloneIndex = 0;
            Map<Integer, Request> requestsWithLP = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                Request req = requests.get(i);
                if (minimalPriority != getLessPriority(req.getSourceNumber(), minimalPriority)) {
                    minimalPriority = req.getSourceNumber();
                    standaloneIndex = i;
                }
            }
            for (int i = 0; i < size; i++) {
                Request req = requests.get(i);
                if (minimalPriority == req.getSourceNumber()) {
                    requestsWithLP.put(i, req);
                }
            }

            if (minimalPriority == getLessPriority(minimalPriority, request.getSourceNumber())) {
                if (requestsWithLP.values().size() == 1) {
                    requests.set(standaloneIndex, request);
                    return false;
                } else if (requestsWithLP.size() > 0) {
                    Map.Entry<Integer, Request> toReplace = null;
                    for (Map.Entry<Integer, Request> entry : requestsWithLP.entrySet()) {
                        if (toReplace == null || toReplace.getValue().getGeneratedTime() > entry.getValue().getGeneratedTime()) {
                            toReplace = entry;
                        }
                    }

                    requests.set(toReplace.getKey(), request);
                    return false;
                }
            }
            return false;
        }
    }

    public boolean isEmpty() {
        return requests.stream().noneMatch(Objects::nonNull) || requests.size() == 0;
    }

    private void incrementPointer() {
        indexPointer++;
        if (indexPointer == size) {
            indexPointer = 0;
        }
    }

    public static void main(String[] args) {
        Buffer buffer = new Buffer(5);
        buffer.addToBuffer(new Request(0, 1, 1));
        buffer.addToBuffer(new Request(2, 2, 2));
        buffer.addToBuffer(new Request(1, 1, 1.5));
        buffer.addToBuffer(new Request(1, 2, 1));
        buffer.addToBuffer(new Request(1, 3, 1.6));
        Main.print(buffer.getRequests() + "\n");
        buffer.addToBuffer(new Request(2, 3, 1));
        Main.print(buffer.getRequests() + "\n");
        buffer.addToBuffer(new Request(0, 3, 3));
        Main.print(buffer.getRequests() + "\n");

        Main.print("");
        //buffer.addToBuffer(new Request(1, 3, 3));
        //buffer.addToBuffer(new Request(1, 4, 4));
        //buffer.addToBuffer(new Request(1, 5, 5));
        Main.print(buffer.get());
        Main.print(buffer.get(2));
        Main.print(buffer.get(2));
        Main.print(buffer.get(2));
        Main.print(buffer.getRequests());
        buffer.addToBuffer(new Request(0, 3, 3));
        Main.print(buffer.getRequests());
        Main.print(buffer.get(1));
        Main.print(buffer.get(1));
        Main.print(buffer.get(0));
        Main.print(buffer.isEmpty());
        Main.print(buffer.getRequests());
    }

    private Integer getLessPriority(Integer first, Integer second) {
        if (first < second) return first;
        else return second;
    }
}
