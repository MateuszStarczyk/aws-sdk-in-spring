package org.pwr.zrcaw_z4.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@Service
public class ComprehendService {

    private static final Region REGION = Region.US_EAST_1;
    private static final String LOCALE = "en";

    public String detectLanguage(String text) {
        ComprehendClient comClient = ComprehendClient.builder()
                .region(REGION)
                .build();


        Locale loc = new Locale(LOCALE);
        String result = "";

        try {

            DetectDominantLanguageRequest request = DetectDominantLanguageRequest.builder()
                    .text(text)
                    .build();

            DetectDominantLanguageResponse resp = comClient.detectDominantLanguage(request);
            List<DominantLanguage> allLanList = resp.languages();
            Iterator<DominantLanguage> lanIterator = allLanList.iterator();

            while (lanIterator.hasNext()) {
                DominantLanguage lang = lanIterator.next();
                result += new Locale(lang.languageCode()).getDisplayLanguage(loc);
            }

        } catch (ComprehendException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return result;
    }

    public List<SyntaxToken> detectAllSyntax(String text){
        ComprehendClient comClient = ComprehendClient.builder()
                .region(REGION)
                .build();

        List<SyntaxToken> tokens = new ArrayList<>();

        try {
            DetectSyntaxRequest detectSyntaxRequest = DetectSyntaxRequest.builder()
                    .text(text)
                    .languageCode(LOCALE)
                    .build();

            DetectSyntaxResponse detectSyntaxResult = comClient.detectSyntax(detectSyntaxRequest);
            List<SyntaxToken> syntaxTokens = detectSyntaxResult.syntaxTokens();
            Iterator<SyntaxToken> syntaxIterator = syntaxTokens.iterator();

            while(syntaxIterator.hasNext()) {
                SyntaxToken token = syntaxIterator.next();
                tokens.add(token);
            }

        } catch (ComprehendException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }

        return tokens;
    }

}
