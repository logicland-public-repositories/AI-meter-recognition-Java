import java.util.regex.Matcher;
import java.util.regex.Pattern;

class example {

    public static class record_results {
        int record;
        float reliability;
        
        record_results(int record, float reliability){
            this.record = record;
            this.reliability = reliability;
        }
    }
    public static void main(String[] args) {
        String url = args[0];
        String login = args[1];
        String password = args[2];
        String image_path = args[3];
        record_recognition records = new record_recognition(url, login, password, image_path);
        try{
            String output = records.get_results();
            int reading = 0;
            float reliability = 0;

            Pattern readingPattern = Pattern.compile("\\{\"reading\":(.*?),");

            Matcher readingmatcher = readingPattern.matcher(output);
            if(readingmatcher.find()){
                reading = Integer.parseInt(readingmatcher.group(1));
            }

            Pattern reliabilityPattern = Pattern.compile("reliability\":(.*?),");
            Matcher reliabilitymatcher = reliabilityPattern.matcher(output);
            if(reliabilitymatcher.find()){
                reliability = Float.parseFloat(reliabilitymatcher.group(1));
            }

        record_results result = new record_results(reading,reliability);
        System.out.println("records: " + result.record);
        System.out.println("reliability: " + result.reliability);
        }catch(Throwable e){
            System.out.println(e.toString());
            System.exit(0);
        }
    }
}
