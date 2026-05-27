package com.mall.service;

import com.mall.entity.Product;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    private static final Map<String, String> KNOWLEDGE = new LinkedHashMap<>();
    static {
        KNOWLEDGE.put("推荐|特产|招牌", "我们为您推荐本店招牌商品：有机农特产、手工制作零食等，品质有保障！");
        KNOWLEDGE.put("烹饪|做法|怎么吃", "烹饪小贴士：新鲜食材建议简单清炒或蒸煮，保留原汁原味。需要详细菜谱可以查看商品详情页哦~");
        KNOWLEDGE.put("保存|存储|保鲜", "生鲜食品建议冷藏保存，干货类请置于阴凉干燥处。开封后请尽快食用。");
        KNOWLEDGE.put("配送|物流|快递|发货", "下单后24小时内发货，一般2-3天送达。冷链配送覆盖主要城市。");
        KNOWLEDGE.put("退货|退款|售后", "支持7天无理由退货，质量问题包邮退换。请联系在线客服处理。");
        KNOWLEDGE.put("优惠|折扣|满减", "关注店铺可领取优惠券，满199减20，满399减50！");
        KNOWLEDGE.put("支付|付款", "支持微信支付、支付宝、银行卡等多种支付方式。");
        KNOWLEDGE.put("运费|包邮", "全场满99元包邮，偏远地区除外。");
        KNOWLEDGE.put("营养|健康", "我们的农产品经过严格质检，绿色无公害，富含多种维生素和矿物质。");
        KNOWLEDGE.put("客服|人工|帮助", "如需人工帮助，请在工作时间(9:00-18:00)联系在线客服。");
    }

    public Map<String, Object> chat(String message) {
        var reply = new StringBuilder("您好！我是智能助手，很高兴为您服务。");
        var matched = new ArrayList<String>();

        for (var entry : KNOWLEDGE.entrySet()) {
            var keywords = entry.getKey().split("\\|");
            for (var kw : keywords) {
                if (message.contains(kw)) {
                    matched.add(entry.getValue());
                    break;
                }
            }
        }
        if (!matched.isEmpty()) reply.append("\n\n").append(String.join("\n\n", matched));
        else reply.append("\n\n您可以咨询商品信息、配送问题、售后服务等，我会尽力帮您解答。");

        var result = new LinkedHashMap<String, Object>();
        result.put("reply", reply.toString());
        result.put("products", new ArrayList<>());
        return result;
    }
}
