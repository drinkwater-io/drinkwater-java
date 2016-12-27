package drinkwater.examples.numberservice;

import java.util.List;

/**
 * Created by A406775 on 27/12/2016.
 */
public class NumberService {

    private INumberRepository numberRepository;

    private INumberFormatter numberFormatter;

    public NumberService(){}

    public NumberService(
            INumberRepository numberRepository,
            INumberFormatter numberFormatter) {
        this.numberRepository = numberRepository;
        this.numberFormatter = numberFormatter;
    }

    public INumberRepository getNumberRepository() {
        return numberRepository;
    }

    public void setNumberRepository(INumberRepository numberRepository) {
        this.numberRepository = numberRepository;
    }

    public INumberFormatter getNumberFormatter() {
        return numberFormatter;
    }

    public void setNumberFormatter(INumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    //API

    public String saveNumber(String filePath, int number){
        try {
            //convert number to string
            String numberAsString = Integer.toString(number);

            //check that length is 10
            while (numberAsString.length() < 5) {
                numberAsString = numberFormatter.prependZero(numberAsString);
            }

            //register the info
            numberRepository.registerSomeInfo(filePath, numberAsString);

            return numberAsString;
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<String> getNumberList(String filePath){
        return numberRepository.getNumbers(filePath);
    }

    public void clear(String filePath){
        numberRepository.clear(filePath);
    }

}
