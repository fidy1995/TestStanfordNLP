package dpp;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.*;

public class TestNLP {
	static LexicalizedParser lp = LexicalizedParser
			.loadModel("parser/chinesePCFG.ser.gz");
	
	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("sighanCorporaDict", "data");
		props.setProperty("serDictionary","scanner/dict-chris6.ser.gz");
		props.setProperty("inputEncoding", "UTF-8");
		props.setProperty("sighanPostProcessing", "true");

		CRFClassifier classifier = new CRFClassifier(props);
		classifier.loadClassifierNoExceptions("scanner/ctb.gz", props);
		classifier.flags.setProperties(props);

		// generate token table
		String sentence = "唯一不是特别好的就是房间都太小了";
		List<String> sent = classifier.segmentString(sentence);
		System.out.println(sent);
		
		// make AST
		Word[] words = new Word[sent.size()];
		int i = 0;
		for (String s: sent) {
			Word w = new Word(s);
			words[i] = w;
			i++;
		}
		Tree parse = (Tree)lp.apply(Arrays.asList(words));
		
		// print AST and semantics
		TreePrint tp = new TreePrint("wordsAndTags, penn, typedDependenciesCollapsed", 
				new ChineseTreebankLanguagePack());
		tp.printTree(parse);
		
		// print semantics collection
		TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		Collection tdl = gs.typedDependenciesCollapsed();
		System.out.println(tdl);
	}
}