package phyloutil.misc;

import phyloutil.NewickParser;
import phyloutil.TreeNode;
import phyloutil.TreeNodeUtil;

public class TestReroot {

	public static void main(String[] args) {
		
		//String str = "(((A:5,B:5)10:2,(C:4,D:4)20:1)30:3,E:10);";
		
		String str = "(Excavate-Euglena_gracilis@blabla:0.09924438538955443478,(Guillardia_theta@blabla:0.52211660649433977444,((Green_algae-Volvox_carteri@blabla:0.13664623506577922019,Green_algae-Chlamydomonas_reinhardtii@blabla:0.17727462032137897285)100:0.38162968161026433744,(((Green_algae-Coccomyxa_sp@blabla:0.00000111354654758020,Green_algae-Chlorella_vulgaris@blabla:0.00000111354654758020)100:0.56654016882158575452,((Fungi-Batrachochytrium_dendrobatidis@blabla:1.40750655236662480796,Fungi-Batrachochytrium_dendrobatidis@blabla:0.40021678730819343350)91:0.32925703085310809026,((Fungi-Neurospora_crassa@blabla:1.10422234581263345454,(Alpha-Candidatus_Pelagibacter@blabla:0.14847696520863976066,Alpha-Candidatus_Pelagibacter@blabla:0.06199441687478528945)100:1.04806649219386849303)55:0.17500515832969926189,(((Aureococcus_anophageferrens@blabla:0.00000111354654758020,Aureococcus_anophageferrens@blabla:0.00000111354654758020)100:0.40919598563183401385,(Diatom-Fragilariopsis_cylindrus@blabla:0.25582679623927190304,Diatom-Phaeodactylum_tricornutum@blabla:0.18129013985698966271)62:0.11870005688392686050)100:0.45869394308260147408,Oomycete-Phytophthora_capsici@blabla:1.19373186346744986075)25:0.12315203189183189514)100:0.42405280398951716858)30:0.07142588754781339921)48:0.15494255283608862617,Haptophyte-Emiliania_huxleyi@blabla:1.02557823103551770672)43:0.07110504720458254990)45:0.10894007625843030174)100:0.62137756847425418272,Excavate-Euglena_gracilis@blabla:0.09893683356074757518);";
		
		TreeNode tree = NewickParser.parse(str);
		
		System.out.println(tree.toString());
		
		TreeNode outgroup = tree.find("Green_algae-Volvox_carteri@blabla");
		
		TreeNode rerooted = TreeNodeUtil.reroot(outgroup);
		
		System.out.println(rerooted);
		
	}

}
